package kitchenpos.dto.request;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;

public class OrderRequestToCreate {

    private long orderTableId;
    private List<OrderLineItemRequestToCreate> orderLineItems;

    private OrderRequestToCreate() {
    }

    public OrderRequestToCreate(final long orderTableId, final List<OrderLineItemRequestToCreate> orderLineItems) {
        this.orderTableId = orderTableId;
        this.orderLineItems = orderLineItems;
    }

    public Order toEntity() {
        final List<OrderLineItem> orderLineItems = this.orderLineItems.stream()
                .map(OrderLineItemRequestToCreate::toEntity)
                .collect(Collectors.toList());
        return new Order(null, orderTableId, null, null, orderLineItems);
    }

    public long getOrderTableId() {
        return this.orderTableId;
    }

    public List<OrderLineItemRequestToCreate> getOrderLineItems() {
        return this.orderLineItems;
    }
}
