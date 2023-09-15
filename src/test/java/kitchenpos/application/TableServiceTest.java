package kitchenpos.application;

import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        OrderTable orderTableRequest = ORDER_TABLE.생성(1L, 1L);
        given(orderTableDao.save(orderTableRequest))
                .willReturn(null);

        // when
        tableService.create(orderTableRequest);

        // then
        Long orderTableId = orderTableRequest.getId();
        Long tableGroupId = orderTableRequest.getTableGroupId();
        assertAll(
                () -> assertThat(orderTableId).isNull(),
                () -> assertThat(tableGroupId).isNull(),
                () -> verify(orderTableDao).save(orderTableRequest)
        );
    }

    @Test
    void 주문_테이블_전체를_조회한다() {
        // given
        given(orderTableDao.findAll())
                .willReturn(null);

        // when
        tableService.list();

        // then
        verify(orderTableDao).findAll();
    }

    @Test
    void 주문_테이블의_자리_상태를_비어있는_상태로_수정한다() {
        // given
        long orderTableIdRequest = 1L;
        OrderTable orderTableRequest = ORDER_TABLE.생성(true);
        OrderTable orderTable = ORDER_TABLE.생성();
        given(orderTableDao.findById(orderTableIdRequest))
                .willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableIdRequest,
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(false);
        given(orderTableDao.save(orderTable))
                .willReturn(null);

        // when
        tableService.changeEmpty(orderTableIdRequest, orderTableRequest);

        // then
        boolean empty = orderTable.isEmpty();
        assertAll(
                () -> assertThat(empty).isTrue(),
                () -> verify(orderTableDao).findById(orderTableIdRequest),
                () -> verify(orderDao).existsByOrderTableIdAndOrderStatusIn(orderTableIdRequest,
                        Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())),
                () -> verify(orderTableDao).save(orderTable)
        );
    }

    @Test
    void 주문_테이블의_자리_상태를_수정할_때_등록되지_않은_주문_테이블이면_예외가_발생한다() {
        // given
        long orderTableId = 1L;
        OrderTable orderTable = ORDER_TABLE.생성();
        given(orderTableDao.findById(orderTableId))
                .willReturn(Optional.empty());

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableService.changeEmpty(orderTableId, orderTable))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findById(orderTableId)
        );
    }

    @Test
    void 주문_테이블의_자리_상태를_수정할_때_테이블_그룹이_있으면_예외가_발생한다() {
        // given
        long orderTableIdRequest = 1L;
        OrderTable orderTableRequest = ORDER_TABLE.생성();
        long orderTableGroupId = 1L;
        OrderTable orderTable = ORDER_TABLE.생성(null, orderTableGroupId);
        given(orderTableDao.findById(orderTableIdRequest))
                .willReturn(Optional.of(orderTable));

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
        OrderTable orderTable = ORDER_TABLE.생성();
        given(orderTableDao.findById(orderTableId))
                .willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableId,
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(true);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableService.changeEmpty(orderTableId, orderTable))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findById(orderTableId),
                () -> verify(orderDao).existsByOrderTableIdAndOrderStatusIn(orderTableId,
                        Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))
        );
    }

    @Test
    void 주문_테이블의_손님_수를_수정한다() {
        // given
        OrderTable orderTableRequest = ORDER_TABLE.생성(2);
        long orderTableIdRequest = 1L;
        OrderTable orderTable = ORDER_TABLE.생성();
        given(orderTableDao.findById(orderTableIdRequest))
                .willReturn(Optional.of(orderTable));
        given(orderTableDao.save(orderTable))
                .willReturn(null);

        // when
        tableService.changeNumberOfGuests(orderTableIdRequest, orderTableRequest);

        // then
        int numberOfGuests = orderTable.getNumberOfGuests();
        assertAll(
                () -> assertThat(numberOfGuests).isEqualTo(2),
                () -> verify(orderTableDao).findById(orderTableIdRequest),
                () -> verify(orderTableDao).save(orderTable)
        );
    }

    @Test
    void 주문_테이블의_손님_수를_음수로_수정하려고_하면_예외가_발생한다_() {
        // given
        OrderTable orderTable = ORDER_TABLE.생성(-1);

        // when, then
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(null, orderTable))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블의_손님_수를_수정할_때_수정할_주문_테이블이_등록되지_않은_경우_예외가_발생한다_() {
        // given
        OrderTable orderTable = ORDER_TABLE.생성();
        given(orderTableDao.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, orderTable))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findById(1L)
        );
    }

    @Test
    void 주문_테이블의_손님_수를_수정할_때_주문_테이블의_자리가_비어있는_경우_예외가_발생한다_() {
        // given
        long orderTableIdRequest = 1L;
        OrderTable orderTableRequest = ORDER_TABLE.생성();
        OrderTable orderTable = ORDER_TABLE.생성(true);
        given(orderTableDao.findById(orderTableIdRequest))
                .willReturn(Optional.of(orderTable));

        // when, then
        assertAll(
                () -> assertThatThrownBy(
                        () -> tableService.changeNumberOfGuests(orderTableIdRequest, orderTableRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findById(orderTableIdRequest)
        );
    }
}
