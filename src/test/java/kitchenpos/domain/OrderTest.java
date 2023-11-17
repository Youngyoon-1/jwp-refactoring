package kitchenpos.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderTest {

    @Test
    void 주문에_포함된_메뉴_id_를_모두_검색한다() {
        // given
        OrderLineItem orderLineItem1 = new OrderLineItem(null, null, 1L, 1L);
        OrderLineItem orderLineItem2 = new OrderLineItem(null, null, 2L, 1L);
        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);
        Order order = new Order(
                null,
                null,
                null,
                null,
                orderLineItems
        );

        // when
        List<Long> menuIds = order.collectMenuIds();

        // then
        Assertions.assertThat(menuIds).containsOnly(1L, 2L);
    }

    @Test
    void 주문을_db_에_저장하기_위해_주문상태를_조리_주문시간을_해당_매서드호출_시간으로_변경한다() {
        Order order = new Order();
        order.updateToSave();
        assertAll(
                () -> Assertions.assertThat(order.getOrderStatus())
                        .isEqualTo(OrderStatus.COOKING.name()),
                () -> Assertions.assertThat(order.getOrderedTime())
                        .isNotNull()
        );
    }
}
