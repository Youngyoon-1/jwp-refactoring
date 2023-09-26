package kitchenpos.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Order;

public class OrderResponse {

    private long id;
    private long orderTableId;
    private String orderStatus;
    private LocalDateTime orderedTime;
    private List<OrderLineItemResponse> orderLineItems;

    private OrderResponse() {
    }

    public OrderResponse(final Order order) {
        this.id = order.getId();
        this.orderTableId = order.getOrderTableId();
        this.orderStatus = order.getOrderStatus();
        this.orderedTime = order.getOrderedTime();
        this.orderLineItems = order.getOrderLineItems().stream()
                .map(OrderLineItemResponse::new)
                .collect(Collectors.toList());
    }

    public long getId() {
        return this.id;
    }

    public long getOrderTableId() {
        return this.orderTableId;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public LocalDateTime getOrderedTime() {
        return this.orderedTime;
    }

    public List<OrderLineItemResponse> getOrderLineItems() {
        return this.orderLineItems;
    }
}
