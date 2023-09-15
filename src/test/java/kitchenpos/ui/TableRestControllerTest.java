package kitchenpos.ui;

import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Collections;
import java.util.List;
import kitchenpos.application.TableService;
import kitchenpos.domain.OrderTable;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(MockitoExtension.class)
class TableRestControllerTest extends UiTest {

    @Mock
    private TableService tableService;

    @InjectMocks
    private TableRestController tableRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = setupMockMvc(tableRestController);
    }

    @Test
    void 주문_테이블을_생성한다() throws Exception {
        // given
        OrderTable orderTableRequest = ORDER_TABLE.생성();
        OrderTable orderTableResponse = ORDER_TABLE.생성(1L);
        BDDMockito.given(tableService.create(ArgumentMatchers.any(OrderTable.class)))
                .willReturn(orderTableResponse);

        // when
        String serializedRequestContent = getObjectMapper().writeValueAsString(orderTableRequest);
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequestContent)
        );

        // then
        String serializedResponseContent = getObjectMapper().writeValueAsString(orderTableResponse);
        assertAll(
                () -> resultActions.andExpect(
                        ResultMatcher.matchAll(
                                MockMvcResultMatchers.status().isCreated(),
                                MockMvcResultMatchers.header().string("location", "/api/tables/1"),
                                MockMvcResultMatchers.content().string(serializedResponseContent)
                        )
                ),
                () -> BDDMockito.verify(tableService).create(ArgumentMatchers.any(OrderTable.class))
        );
    }

    @Test
    void 주문_테이블_전체를_조회한다() throws Exception {
        // given
        OrderTable orderTable = ORDER_TABLE.생성();
        List<OrderTable> orderTables = Collections.singletonList(orderTable);
        BDDMockito.given(tableService.list())
                .willReturn(orderTables);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tables")
        );

        // then
        String serializedContent = getObjectMapper().writeValueAsString(orderTables);
        assertAll(
                () -> resultActions.andExpect(
                        ResultMatcher.matchAll(
                                MockMvcResultMatchers.status().isOk(),
                                MockMvcResultMatchers.content().string(serializedContent)
                        )
                ),
                () -> BDDMockito.verify(tableService).list()
        );
    }

    @Test
    void 주문_테이블의_자리_상태를_변경한다() throws Exception {
        // given
        long orderTableId = 1L;
        OrderTable orderTable = ORDER_TABLE.생성();
        BDDMockito.given(
                        tableService.changeEmpty(ArgumentMatchers.eq(orderTableId), ArgumentMatchers.any(OrderTable.class)))
                .willReturn(orderTable);
        String serializedContent = getObjectMapper().writeValueAsString(orderTable);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/tables/1/empty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedContent)
        );

        // then
        assertAll(
                () -> resultActions.andExpect(
                        ResultMatcher.matchAll(
                                MockMvcResultMatchers.status().isOk(),
                                MockMvcResultMatchers.content().string(serializedContent)
                        )
                ),
                () -> BDDMockito.verify(tableService)
                        .changeEmpty(ArgumentMatchers.eq(orderTableId), ArgumentMatchers.any(OrderTable.class))
        );
    }

    @Test
    void 주문_테이블의_손님_수를_변경한다() throws Exception {
        // given
        long orderTableId = 1L;
        OrderTable orderTable = ORDER_TABLE.생성();
        BDDMockito.given(tableService.changeNumberOfGuests(ArgumentMatchers.eq(orderTableId),
                        ArgumentMatchers.any(OrderTable.class)))
                .willReturn(orderTable);
        String serializedContent = getObjectMapper().writeValueAsString(orderTable);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/tables/1/number-of-guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedContent)
        );

        // then
        assertAll(
                () -> resultActions.andExpect(
                        ResultMatcher.matchAll(
                                MockMvcResultMatchers.status().isOk(),
                                MockMvcResultMatchers.content().string(serializedContent)
                        )
                ),
                () -> BDDMockito.verify(tableService)
                        .changeNumberOfGuests(ArgumentMatchers.eq(orderTableId), ArgumentMatchers.any(OrderTable.class))
        );
    }
}
