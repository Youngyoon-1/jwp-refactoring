package kitchenpos.dto.request;

import kitchenpos.domain.OrderLineItem;

public class OrderLineItemRequestToCreate {

    private long menuId;
    private long quantity;

    private OrderLineItemRequestToCreate() {
    }

    public OrderLineItemRequestToCreate(final long menuId, final long quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public OrderLineItem toEntity() {
        return new OrderLineItem(null, null, menuId, quantity);
    }

    public long getMenuId() {
        return this.menuId;
    }

    public long getQuantity() {
        return this.quantity;
    }
}
