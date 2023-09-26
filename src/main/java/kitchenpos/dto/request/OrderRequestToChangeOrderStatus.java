package kitchenpos.dto.request;

public class OrderRequestToChangeOrderStatus {

    private String orderStatus;

    private OrderRequestToChangeOrderStatus() {
    }

    public OrderRequestToChangeOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }
}
