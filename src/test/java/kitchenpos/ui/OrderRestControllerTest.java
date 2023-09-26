package kitchenpos.ui;

import static kitchenpos.domain.OrderStatus.MEAL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kitchenpos.application.OrderService;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.dto.request.OrderLineItemRequestToCreate;
import kitchenpos.dto.request.OrderRequestToChangeOrderStatus;
import kitchenpos.dto.request.OrderRequestToCreate;
import kitchenpos.dto.response.OrderResponse;
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
        OrderLineItem orderLineItem = new OrderLineItem(1L, 1L, 1L, 1L);
        List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItem);
        Order order = new Order(1L, 1L, MEAL.name(), LocalDateTime.now(), orderLineItems);
        OrderResponse orderResponse = new OrderResponse(order);
        given(orderService.create(any(OrderRequestToCreate.class)))
                .willReturn(orderResponse);

        // when
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(1L, 1L);
        List<OrderLineItemRequestToCreate> orderLineItemRequests = Collections.singletonList(orderLineItemRequest);
        OrderRequestToCreate orderRequest = new OrderRequestToCreate(1L, orderLineItemRequests);
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
                () -> BDDMockito.verify(orderService).create(any(OrderRequestToCreate.class))
        );
    }

    @Test
    void 주문_전체를_조회한다() throws Exception {
        // given
        Order order = new Order(1L, 1L, OrderStatus.MEAL.name(), LocalDateTime.now(), new ArrayList<>());
        OrderResponse orderResponse = new OrderResponse(order);
        List<OrderResponse> orderResponses = Collections.singletonList(orderResponse);
        given(orderService.list())
                .willReturn(orderResponses);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        String serializedContent = getObjectMapper().writeValueAsString(orderResponses);
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
        long orderId = 1L;
        Order savedOrder = new Order(orderId, 1L, OrderStatus.MEAL.name(), LocalDateTime.now(), new ArrayList<>());
        OrderResponse orderResponse = new OrderResponse(savedOrder);
        given(orderService.changeOrderStatus(ArgumentMatchers.eq(orderId), any(OrderRequestToChangeOrderStatus.class)))
                .willReturn(orderResponse);

        // when
        OrderRequestToChangeOrderStatus orderRequest = new OrderRequestToChangeOrderStatus(OrderStatus.MEAL.name());
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
                        .changeOrderStatus(ArgumentMatchers.eq(orderId), any(OrderRequestToChangeOrderStatus.class))
        );
    }
}
