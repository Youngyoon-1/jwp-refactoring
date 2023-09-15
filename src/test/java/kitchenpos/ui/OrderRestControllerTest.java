package kitchenpos.ui;

import static kitchenpos.domain.OrderStatus.MEAL;
import static kitchenpos.support.fixture.OrderFixture.ORDER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import kitchenpos.application.OrderService;
import kitchenpos.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(MockitoExtension.class)
class OrderRestControllerTest extends UiTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderRestController orderRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = setupMockMvc(orderRestController);
    }

    @Test
    void 주문을_생성한다() throws Exception {
        // given
        Order orderRequest = ORDER.생성();
        Order orderResponse = ORDER.생성(1L);
        given(orderService.create(any(Order.class)))
                .willReturn(orderResponse);

        // when
        String serializedRequestContent = getObjectMapper().writeValueAsString(orderRequest);
        ResultActions resultActions = mockMvc.perform(
                post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequestContent)
        );

        // then
        String serializedResponseContent = getObjectMapper().writeValueAsString(orderResponse);
        assertAll(
                () -> resultActions.andExpect(
                        matchAll(
                                status().isCreated(),
                                header().stringValues("location", "/api/orders/1"),
                                content().string(serializedResponseContent)
                        )
                ),
                () -> BDDMockito.verify(orderService).create(any(Order.class))
        );
    }

    @Test
    void 주문_전체를_조회한다() throws Exception {
        // given
        Order order = ORDER.생성();
        List<Order> orderResponse = Collections.singletonList(order);
        given(orderService.list())
                .willReturn(orderResponse);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        String serializedContent = getObjectMapper().writeValueAsString(orderResponse);
        assertAll(
                () -> resultActions.andExpect(
                        matchAll(
                                status().isOk(),
                                content().string(serializedContent)
                        )
                ),
                () -> BDDMockito.verify(orderService).list()
        );
    }

    @Test
    void 주문_상태를_식사로_변경한다() throws Exception {
        // given
        long orderIdRequest = 1L;
        Order orderRequest = ORDER.생성(MEAL.toString());
        Order orderResponse = ORDER.생성(MEAL.toString());
        given(orderService.changeOrderStatus(ArgumentMatchers.anyLong(), any(Order.class)))
                .willReturn(orderResponse);

        // when
        String serializedRequestContent = getObjectMapper().writeValueAsString(orderRequest);
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/orders/1/order-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequestContent)
        );

        // then
        String serializedResponseContent = getObjectMapper().writeValueAsString(orderResponse);
        assertAll(
                () -> resultActions.andExpect(
                        matchAll(
                                status().isOk(),
                                content().string(serializedResponseContent)
                        )
                ),
                () -> BDDMockito.verify(orderService)
                        .changeOrderStatus(ArgumentMatchers.anyLong(), any(Order.class))
        );
    }
}
