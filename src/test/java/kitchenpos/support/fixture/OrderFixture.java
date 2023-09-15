package kitchenpos.support.fixture;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public enum OrderFixture {

    ORDER,
    ;

    public Order 조리_상태로_생성(final OrderTable orderTable) {
        final Order order = new Order();
        order.setOrderTableId(orderTable.getId());
        order.setOrderStatus(OrderStatus.COOKING.toString());
        order.setOrderedTime(LocalDateTime.now());

        return order;
    }

    public Order 식사_상태로_생성(final OrderTable orderTable) {
        final Order order = new Order();
        order.setOrderTableId(orderTable.getId());
        order.setOrderStatus(OrderStatus.MEAL.toString());
        order.setOrderedTime(LocalDateTime.now());

        return order;
    }

    public Order 생성() {
        return 생성(null, null, null, null);
    }

    public Order 생성(final String orderStatus) {
        return 생성(null, null, null, orderStatus);
    }

    public Order 생성(final Long id) {
        return 생성(id, null, null, null);
    }

    public Order 생성(final List<OrderLineItem> orderLineItems) {
        return 생성(null, null, orderLineItems, null);
    }

    public Order 생성(final Long orderTableId, final List<OrderLineItem> orderLineItems) {
        return 생성(null, orderTableId, orderLineItems, null);
    }

    public Order 생성(final Long id, final Long orderTableId) {
        return 생성(id, orderTableId, null, null);
    }

    public Order 생성(final Long id, final Long orderTableId, final List<OrderLineItem> orderLineItems) {
        return 생성(id, orderTableId, orderLineItems, null);
    }

    public Order 생성(final Long id, final Long orderTableId, final List<OrderLineItem> orderLineItems,
                    final String orderStatus) {
        final Order order = new Order();
        order.setId(id);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(orderLineItems);
        order.setOrderStatus(orderStatus);

        return order;
    }
}
