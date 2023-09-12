package kitchenpos.support.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;

public enum OrderLineItemFixture {

    ORDER_LINE_ITEM_1(1),
    ORDER_LINE_ITEM_2(1),
    ;

    private final int quantity;

    OrderLineItemFixture(final int quantity) {
        this.quantity = quantity;
    }

    public OrderLineItem 생성(final Order order, final Menu menu) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setOrderId(order.getId());
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(this.quantity);

        return orderLineItem;
    }
}
