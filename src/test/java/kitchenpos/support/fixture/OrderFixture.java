package kitchenpos.support.fixture;

import java.time.LocalDateTime;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public enum OrderFixture {

    ORDER_1,
    ORDER_2,
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
}
