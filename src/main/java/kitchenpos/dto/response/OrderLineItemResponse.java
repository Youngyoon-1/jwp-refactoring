package kitchenpos.dto.response;

import kitchenpos.domain.OrderLineItem;

public class OrderLineItemResponse {

    private long seq;
    private long orderId;
    private long menuId;
    private long quantity;

    private OrderLineItemResponse() {
    }

    public OrderLineItemResponse(final OrderLineItem orderLineItem) {
        this.seq = orderLineItem.getSeq();
        this.orderId = orderLineItem.getOrderId();
        this.menuId = orderLineItem.getMenuId();
        this.quantity = orderLineItem.getQuantity();
    }

    public Long getSeq() {
        return this.seq;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public Long getMenuId() {
        return this.menuId;
    }

    public Long getQuantity() {
        return this.quantity;
    }
}
