package kitchenpos.application;

import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.request.TableRequestToChangeEmpty;
import kitchenpos.dto.request.TableRequestToChangeNumberOfGuests;
import kitchenpos.dto.request.TableRequestToCreate;
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
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableService tableService;

    @Test
    void 주문_테이블을_등록한다() {
        // given
        OrderTable orderTable = new OrderTable(1L, null, 1, true);
        given(orderTableDao.save(ArgumentMatchers.any(OrderTable.class)))
                .willReturn(orderTable);

        // when
        TableRequestToCreate orderTableRequest = new TableRequestToCreate(1, false);
        tableService.create(orderTableRequest);

        // then
        BDDMockito.verify(orderTableDao).save(ArgumentMatchers.any(OrderTable.class));
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
        tableService.list();

        // then
        verify(orderTableDao).findAll();
    }

    @Test
    void 주문_테이블의_자리_상태를_비어있는_상태로_수정한다() {
        // given
        long orderTableId = 1L;
        OrderTable orderTable = new OrderTable(orderTableId, null, 1, false);
        given(orderTableDao.findById(orderTableId))
                .willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableId,
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(false);
        given(orderTableDao.save(orderTable))
                .willReturn(orderTable);

        // when
        TableRequestToChangeEmpty orderTableRequest = new TableRequestToChangeEmpty(true);
        tableService.changeEmpty(orderTableId, orderTableRequest);

        // then
        boolean empty = orderTable.isEmpty();
        assertAll(
                () -> assertThat(empty).isTrue(),
                () -> verify(orderTableDao).findById(orderTableId),
                () -> verify(orderDao).existsByOrderTableIdAndOrderStatusIn(orderTableId,
                        Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())),
                () -> verify(orderTableDao).save(orderTable)
        );
    }

    @Test
    void 주문_테이블의_자리_상태를_수정할_때_등록되지_않은_주문_테이블이면_예외가_발생한다() {
        // given
        long orderTableId = 1L;
        TableRequestToChangeEmpty orderTableRequest = new TableRequestToChangeEmpty(true);
        given(orderTableDao.findById(orderTableId))
                .willReturn(Optional.empty());

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableService.changeEmpty(orderTableId, orderTableRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findById(orderTableId)
        );
    }

    @Test
    void 주문_테이블의_자리_상태를_수정할_때_테이블_그룹이_있으면_예외가_발생한다() {
        // given
        long orderTableIdRequest = 1L;
        TableRequestToChangeEmpty orderTableRequest = new TableRequestToChangeEmpty(true);
        long orderTableGroupId = 1L;
        OrderTable orderTableToWillReturn = ORDER_TABLE.생성(null, orderTableGroupId);
        given(orderTableDao.findById(orderTableIdRequest))
                .willReturn(Optional.of(orderTableToWillReturn));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableService.changeEmpty(orderTableIdRequest, orderTableRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findById(orderTableIdRequest)
        );
    }

    @Test
    void 주문_테이블의_자리_상태를_수정할_때_주문의_상태가_조리_또는_식사인_경우_예외가_발생한다() {
        // given
        long orderTableId = 1L;
        TableRequestToChangeEmpty orderTableRequest = new TableRequestToChangeEmpty(true);
        OrderTable orderTableToWillReturn = ORDER_TABLE.생성();
        given(orderTableDao.findById(orderTableId))
                .willReturn(Optional.of(orderTableToWillReturn));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableId,
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(true);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableService.changeEmpty(orderTableId, orderTableRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findById(orderTableId),
                () -> verify(orderDao).existsByOrderTableIdAndOrderStatusIn(orderTableId,
                        Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))
        );
    }

    @Test
    void 주문_테이블의_손님_수를_수정한다() {
        // given
        long orderTableId = 1L;
        OrderTable selectedOrderTable = new OrderTable(orderTableId, null, 1, false);
        given(orderTableDao.findById(orderTableId))
                .willReturn(Optional.of(selectedOrderTable));
        OrderTable savedOrderTable = new OrderTable(orderTableId, null, 2, false);
        given(orderTableDao.save(selectedOrderTable))
                .willReturn(savedOrderTable);

        // when
        TableRequestToChangeNumberOfGuests tableRequest = new TableRequestToChangeNumberOfGuests(2);
        tableService.changeNumberOfGuests(orderTableId, tableRequest);

        // then
        int actualNumberOfGuests = selectedOrderTable.getNumberOfGuests();
        int expectationNumberOfGuests = tableRequest.getNumberOfGuests();
        assertAll(
                () -> assertThat(actualNumberOfGuests).isEqualTo(expectationNumberOfGuests),
                () -> verify(orderTableDao).findById(orderTableId),
                () -> verify(orderTableDao).save(selectedOrderTable)
        );
    }

    @Test
    void 주문_테이블의_손님_수를_음수로_수정하려고_하면_예외가_발생한다_() {
        // given
        TableRequestToChangeNumberOfGuests tableRequest = new TableRequestToChangeNumberOfGuests(-1);

        // when, then
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(null, tableRequest))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블의_손님_수를_수정할_때_수정할_주문_테이블이_등록되지_않은_경우_예외가_발생한다_() {
        // given
        TableRequestToChangeNumberOfGuests tableRequest = new TableRequestToChangeNumberOfGuests(1);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, tableRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findById(1L)
        );
    }

    @Test
    void 주문_테이블의_손님_수를_수정할_때_주문_테이블의_자리가_비어있는_경우_예외가_발생한다_() {
        // given
        long orderTableIdRequest = 1L;
        OrderTable orderTable = ORDER_TABLE.생성(true);
        given(orderTableDao.findById(orderTableIdRequest))
                .willReturn(Optional.of(orderTable));

        // when, then
        TableRequestToChangeNumberOfGuests tableRequest = new TableRequestToChangeNumberOfGuests(1);
        assertAll(
                () -> assertThatThrownBy(
                        () -> tableService.changeNumberOfGuests(orderTableIdRequest, tableRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findById(orderTableIdRequest)
        );
    }
}
