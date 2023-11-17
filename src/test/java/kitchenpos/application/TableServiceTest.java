package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.TableValidator;
import kitchenpos.dto.request.TableRequestToChangeEmpty;
import kitchenpos.dto.request.TableRequestToChangeNumberOfGuests;
import kitchenpos.dto.request.TableRequestToCreate;
import kitchenpos.dto.response.OrderTableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Mock
    private TableValidator tableValidator;

    @Mock
    private OrderTableRepository orderTableDao;

    @InjectMocks
    private TableService tableService;

    @Test
    void 주문_테이블을_등록한다() {
        // given
        OrderTable orderTable = new OrderTable(1L, null, 1, false);
        given(orderTableDao.save(ArgumentMatchers.any(OrderTable.class)))
                .willReturn(orderTable);

        // when
        TableRequestToCreate orderTableRequest = new TableRequestToCreate(1, false);
        OrderTableResponse response = tableService.create(orderTableRequest);

        // then
        long orderTableId = response.getId();
        Long tableGroupId = response.getTableGroupId();
        int numberOfGuests = response.getNumberOfGuests();
        boolean empty = response.isEmpty();
        assertAll(
                () -> BDDMockito.verify(orderTableDao).save(ArgumentMatchers.any(OrderTable.class)),
                () -> Assertions.assertThat(orderTableId).isEqualTo(1L),
                () -> Assertions.assertThat(tableGroupId).isNull(),
                () -> Assertions.assertThat(numberOfGuests).isEqualTo(1L),
                () -> Assertions.assertThat(empty).isFalse()
        );
    }

    @Test
    void 주문_테이블_전체를_조회한다() {
        // given
        List<OrderTable> orderTables = Collections.singletonList(
                new OrderTable(1L, null, 1, false)
        );
        given(orderTableDao.findAll())
                .willReturn(orderTables);

        // when
        List<OrderTableResponse> response = tableService.list();

        // then
        OrderTableResponse orderTableResponse = response.get(0);
        long orderTableId = orderTableResponse.getId();
        Long tableGroupId = orderTableResponse.getTableGroupId();
        int numberOfGuests = orderTableResponse.getNumberOfGuests();
        boolean empty = orderTableResponse.isEmpty();
        assertAll(
                () -> verify(orderTableDao).findAll(),
                () -> Assertions.assertThat(orderTableId).isEqualTo(1L),
                () -> Assertions.assertThat(tableGroupId).isNull(),
                () -> Assertions.assertThat(numberOfGuests).isEqualTo(1L),
                () -> Assertions.assertThat(empty).isFalse()
        );
    }

    @Test
    void 주문_테이블의_자리_상태를_비어있는_상태로_수정한다() {
        // given
        OrderTable orderTable = new OrderTable(1L, null, 0, false);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.of(orderTable));
        BDDMockito.willDoNothing()
                .given(tableValidator)
                .validateToChangeEmpty(orderTable);

        // when
        TableRequestToChangeEmpty orderTableRequest = new TableRequestToChangeEmpty(true);
        OrderTableResponse response = tableService.changeEmpty(1L, orderTableRequest);

        // then
        long orderTableId = response.getId();
        Long tableGroupId = response.getTableGroupId();
        int numberOfGuests = response.getNumberOfGuests();
        boolean empty = response.isEmpty();
        assertAll(
                () -> Assertions.assertThat(orderTableId).isEqualTo(1L),
                () -> Assertions.assertThat(tableGroupId).isNull(),
                () -> Assertions.assertThat(numberOfGuests).isZero(),
                () -> assertThat(empty).isTrue(),
                () -> verify(orderTableDao).findById(orderTableId),
                () -> verify(tableValidator).validateToChangeEmpty(orderTable)
        );
    }

    @Test
    void 주문_테이블의_자리_상태를_수정할_때_등록되지_않은_주문_테이블이면_예외가_발생한다() {
        // given
        given(orderTableDao.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        TableRequestToChangeEmpty orderTableRequest = new TableRequestToChangeEmpty(true);
        assertAll(
                () -> assertThatThrownBy(() -> tableService.changeEmpty(1L, orderTableRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("주문 테이블의 자리 상태를 변경할 때 주문 테이블이 저장되어 있어야 합니다."),
                () -> verify(orderTableDao).findById(1L)
        );
    }

    @Test
    void 주문_테이블의_손님_수를_수정한다() {
        // given
        OrderTable selectedOrderTable = new OrderTable(1L, null, 1, false);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.of(selectedOrderTable));

        // when
        TableRequestToChangeNumberOfGuests request = new TableRequestToChangeNumberOfGuests(2);
        OrderTableResponse response = tableService.changeNumberOfGuests(1L, request);

        // then
        long orderTableId = response.getId();
        Long tableGroupId = response.getTableGroupId();
        int numberOfGuests = response.getNumberOfGuests();
        boolean empty = response.isEmpty();
        assertAll(
                () -> Assertions.assertThat(orderTableId).isEqualTo(1L),
                () -> Assertions.assertThat(tableGroupId).isNull(),
                () -> Assertions.assertThat(numberOfGuests).isEqualTo(2),
                () -> assertThat(empty).isFalse(),
                () -> verify(orderTableDao).findById(orderTableId)
        );
    }

    @Test
    void 주문_테이블의_손님_수를_음수로_수정하려고_하면_예외가_발생한다_() {
        TableRequestToChangeNumberOfGuests request = new TableRequestToChangeNumberOfGuests(-1);
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(null, request))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 테이블의 손님 수를 변경할 때 변경할 수가 음수면 안됩니다.");
    }

    @Test
    void 주문_테이블의_손님_수를_수정할_때_수정할_주문_테이블이_등록되지_않은_경우_예외가_발생한다_() {
        // given
        given(orderTableDao.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        TableRequestToChangeNumberOfGuests reqeust = new TableRequestToChangeNumberOfGuests(1);
        assertAll(
                () -> assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, reqeust))
                        .isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("주문 테이블의 손님 수를 변경할 때 주문 테이블이 저장되어 있어야 합니다."),
                () -> verify(orderTableDao).findById(1L)
        );
    }
}
