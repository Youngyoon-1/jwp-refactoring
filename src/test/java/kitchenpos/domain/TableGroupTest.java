package kitchenpos.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TableGroupTest {

    @Test
    void 테이블_그룹을_저장하기_위해_테이블_그룹을_생성한다_생성_시간을_초기화한다() {
        OrderTable orderTable1 = OrderTable.createToSaveTableGroup(1L);
        OrderTable orderTable2 = OrderTable.createToSaveTableGroup(2L);
        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);
        LocalDateTime createdDate = TableGroup.createToSave(orderTables)
                .getCreatedDate();
        Assertions.assertThat(createdDate).isNotNull();
    }

    @Test
    void 테이블_그룹을_저장하기_위해_테이블_그룹을_생성시_주문_테이블이_null_이면_예외가_발생한다() {
        Assertions.assertThatThrownBy(
                () -> TableGroup.createToSave(null)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 테이블_그룹을_저장하기_위해_테이블_그룹을_생성시_주문_테이블이_2개_미만이면_예외가_발생한다() {
        OrderTable orderTable = OrderTable.createToSaveTableGroup(1L);
        List<OrderTable> orderTables = Collections.singletonList(orderTable);
        Assertions.assertThatThrownBy(
                () -> TableGroup.createToSave(orderTables)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 테이블_그룹이_테이블_그룹_저장을_위한_유효성_검증시_테이블_그룹의_주문_테이블_수와_인자값인_n_개의_주문_테이블_수가_같다면_예외가_발생하지_않는다() {
        OrderTable orderTable1 = OrderTable.createToSaveTableGroup(1L);
        OrderTable orderTable2 = OrderTable.createToSaveTableGroup(2L);
        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);
        TableGroup tableGroup = TableGroup.createToSave(orderTables);
        Assertions.assertThatCode(
                () -> tableGroup.validateToSave(orderTables)
        ).doesNotThrowAnyException();
    }

    @Test
    void 테이블_그룹이_테이블_그룹_저장을_위한_유효성_검증시_테이블_그룹의_주문_테이블_수와_인자값인_주문_테이블의_수가_다르면_예외가_발생한다() {
        OrderTable orderTable1 = OrderTable.createToSaveTableGroup(1L);
        OrderTable orderTable2 = OrderTable.createToSaveTableGroup(2L);
        TableGroup tableGroup = TableGroup.createToSave(Arrays.asList(orderTable1, orderTable2));
        Assertions.assertThatThrownBy(
                () -> tableGroup.validateToSave(new ArrayList<>())
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
