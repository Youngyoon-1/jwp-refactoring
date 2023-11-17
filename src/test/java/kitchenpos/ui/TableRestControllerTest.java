package kitchenpos.ui;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Collections;
import java.util.List;
import kitchenpos.application.TableService;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.request.TableRequestToChangeEmpty;
import kitchenpos.dto.request.TableRequestToChangeNumberOfGuests;
import kitchenpos.dto.request.TableRequestToCreate;
import kitchenpos.dto.response.OrderTableResponse;
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
        OrderTable orderTableRequest = new OrderTable(null, null, 1, false);
        OrderTable orderTable = new OrderTable(1L, null, 1, false);
        OrderTableResponse orderTableResponse = new OrderTableResponse(orderTable);
        BDDMockito.given(tableService.create(ArgumentMatchers.any(TableRequestToCreate.class)))
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
                () -> BDDMockito.verify(tableService).create(ArgumentMatchers.any(TableRequestToCreate.class))
        );
    }

    @Test
    void 주문_테이블_전체를_조회한다() throws Exception {
        // given
        OrderTable orderTable = new OrderTable(1L, null, 1, false);
        List<OrderTableResponse> orderTableResponses = Collections.singletonList(new OrderTableResponse(orderTable));
        BDDMockito.given(tableService.list())
                .willReturn(orderTableResponses);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tables")
        );

        // then
        String serializedContent = getObjectMapper().writeValueAsString(orderTableResponses);
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
        OrderTable orderTable = new OrderTable(1L, null, 1, false);
        OrderTableResponse orderTableResponse = new OrderTableResponse(orderTable);
        BDDMockito.given(
                tableService.changeEmpty(
                        ArgumentMatchers.eq(1L),
                        ArgumentMatchers.any(TableRequestToChangeEmpty.class)
                )
        ).willReturn(orderTableResponse);

        // when
        String serializedRequest = getObjectMapper().writeValueAsString(new TableRequestToChangeEmpty(false));
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/tables/1/empty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequest)
        );

        // then
        String serializedResponse = getObjectMapper().writeValueAsString(orderTableResponse);
        assertAll(
                () -> resultActions.andExpect(
                        ResultMatcher.matchAll(
                                MockMvcResultMatchers.status().isOk(),
                                MockMvcResultMatchers.content().string(serializedResponse)
                        )
                ),
                () -> BDDMockito.verify(tableService)
                        .changeEmpty(
                                ArgumentMatchers.eq(1L),
                                ArgumentMatchers.any(TableRequestToChangeEmpty.class)
                        )
        );
    }

    @Test
    void 주문_테이블의_손님_수를_변경한다() throws Exception {
        // given
        OrderTable orderTable = new OrderTable(1L, null, 1, false);
        OrderTableResponse orderTableResponse = new OrderTableResponse(orderTable);
        BDDMockito.given(tableService.changeNumberOfGuests(
                        ArgumentMatchers.eq(1L),
                        ArgumentMatchers.any(TableRequestToChangeNumberOfGuests.class)
                )
        ).willReturn(orderTableResponse);

        // when
        TableRequestToChangeNumberOfGuests tableRequest = new TableRequestToChangeNumberOfGuests(1);
        String serializedRequest = getObjectMapper().writeValueAsString(tableRequest);
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/tables/1/number-of-guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequest)
        );

        // then
        String serializedResponseContent = getObjectMapper().writeValueAsString(orderTableResponse);
        assertAll(
                () -> resultActions.andExpect(
                        ResultMatcher.matchAll(
                                MockMvcResultMatchers.status().isOk(),
                                MockMvcResultMatchers.content().string(serializedResponseContent)
                        )
                ),
                () -> BDDMockito.verify(tableService)
                        .changeNumberOfGuests(
                                ArgumentMatchers.eq(1L),
                                ArgumentMatchers.any(TableRequestToChangeNumberOfGuests.class)
                        )
        );
    }
}
