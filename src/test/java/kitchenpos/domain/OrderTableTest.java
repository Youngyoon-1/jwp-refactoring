package kitchenpos.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderTableTest {

    @Test
    void 주문_테이블의_테이블_그룹을_해제한다() {
        // given
        long tableGroupId = 1L;
        boolean empty = true;
        OrderTable orderTable = new OrderTable(null, tableGroupId, 0, empty);

        // when
        orderTable.ungroup();

        // then
        assertAll(
                () -> Assertions.assertThat(orderTable.getTableGroupId()).isNull(),
                () -> Assertions.assertThat(orderTable.isEmpty()).isFalse()
        );
    }
}
