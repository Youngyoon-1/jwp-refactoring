package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderTableTest {

    @Test
    void 주문_테이블을_생성하면_id_와_table_group_id_를_null_로_초기화_한다() {
        OrderTable orderTable = new OrderTable();
        Long id = orderTable.getId();
        Long tableGroupId = orderTable.getTableGroupId();
        assertAll(
                () -> assertThat(id).isNull(),
                () -> assertThat(tableGroupId).isNull()
        );
    }

    @Test
    void 주문_테이블의_table_group_id_가_null_이_아니면_예외가_발생한다() {
        OrderTable orderTable = new OrderTable(1L, 1L, 1, false);
        Assertions.assertThatThrownBy(orderTable::validateToUpdateEmpty)
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블의_table_group_id_가_null_이면_예외가_발생하지_않는다() {
        OrderTable orderTable = new OrderTable(1L, null, 1, false);
        assertThatCode(orderTable::validateToUpdateEmpty)
                .doesNotThrowAnyException();
    }

    @Test
    void 손님_수를_수정하기_위한_주문_테이블을_생성한다() {
        assertThatCode(
                () -> OrderTable.createToUpdateNumberOfGuests(0)
        ).doesNotThrowAnyException();
    }

    @Test
    void 손님_수를_수정하기_위한_주문_테이블_생성시_손님_수가_0_미만_이면_예외가_발생한다() {
        Assertions.assertThatThrownBy(
                () -> OrderTable.createToUpdateNumberOfGuests(-1)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블의_손님_수를_수정할_때_해당_주문_테이블의_자리가_비어있지_않다면_예외가_발생하지_않는다() {
        OrderTable target = new OrderTable(null, null, 1, false);
        OrderTable other = new OrderTable(null, null, 0, false);
        assertThatCode(
                () -> target.updateNumberOfGuests(other)
        ).doesNotThrowAnyException();
    }

    @Test
    void 주문_테이블의_손님_수를_수정할_때_해당_주문_테이블의_자리가_비어있으면_예외가_발생한다() {
        OrderTable target = new OrderTable(null, null, 0, true);
        OrderTable other = new OrderTable();
        Assertions.assertThatThrownBy(
                () -> target.updateNumberOfGuests(other)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_생성을_위한_주문_테이블_유효성_검증시_주문_테이블의_자리가_비어있지_않으면_예외가_발생하지_않는다() {
        OrderTable orderTable = new OrderTable(null, null, 1, false);
        Assertions.assertThatCode(orderTable::validateToSaveOrder)
                .doesNotThrowAnyException();
    }

    @Test
    void 주문_생성을_위한_주문_테이블_유효성_검증시_주문_테이블의_자리가_비어있으면_예외가_발생한다() {
        OrderTable orderTable = new OrderTable(null, null, 0, true);
        Assertions.assertThatThrownBy(
                () -> orderTable.validateToSaveOrder()
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
