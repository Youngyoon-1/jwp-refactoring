package kitchenpos.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderTablesTest {

    @Test
    void 메뉴_그룹을_저장하기_위한_OrderTables_를_생성한다_주문_테이블의_자리_상태를_false_로_초기화된다() {
        // when
        OrderTable orderTable = new OrderTable(1L, null, 0, true);
        OrderTables orderTables = OrderTables.createToSaveTableGroup(Collections.singletonList(orderTable));
        boolean actualEmpty = orderTables.getOrderTables()
                .get(0)
                .isEmpty();

        // then
        Assertions.assertThat(actualEmpty).isFalse();
    }

    @Test
    void 메뉴_그룹을_저장하기_위한_OrderTables_를_생성할_떄_n_개의_테이블_그룹_id_중_하나라도_null_이_아닌_경우_예외가_발생한다() {
        OrderTable orderTable = new OrderTable(1L, 1L, 0, true);
        List<OrderTable> orderTables = Collections.singletonList(orderTable);
        Assertions.assertThatThrownBy(
                () -> OrderTables.createToSaveTableGroup(orderTables)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_그룹을_저장하기_위한_OrderTables_를_생성할_때_n_개의_주문_테이블의_자리_상태가_하나라도_비어있지_않으면_예외가_발생한다() {
        OrderTable orderTable = new OrderTable(1L, null, 1, false);
        List<OrderTable> orderTables = Collections.singletonList(orderTable);
        Assertions.assertThatThrownBy(
                () -> OrderTables.createToSaveTableGroup(orderTables)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 그룹_해제를_위한_OrderTables_생성시_테이블_그룹_아이디를_null_로_자리_상태를_비어있지_않은_상태로_초기화한다() {
        // when
        OrderTable orderTable1 = new OrderTable(1L, 1L, 0, true);
        OrderTable orderTable2 = new OrderTable(2L, 1L, 0, true);
        OrderTables orderTables = OrderTables.createToUngroup(Arrays.asList(orderTable1, orderTable2));

        // then
        List<OrderTable> actualOrderTables = orderTables.getOrderTables();
        List<Long> actualTableGroupIds = actualOrderTables.stream()
                .map(OrderTable::getTableGroupId)
                .collect(Collectors.toList());
        List<Boolean> actualTableGroupEmpties = actualOrderTables.stream()
                .map(OrderTable::isEmpty)
                .collect(Collectors.toList());
        assertAll(
                () -> Assertions.assertThat(actualTableGroupIds).containsOnlyNulls(),
                () -> Assertions.assertThat(actualTableGroupEmpties).containsOnly(Boolean.FALSE)
        );
    }
}
