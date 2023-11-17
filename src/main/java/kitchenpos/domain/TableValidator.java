package kitchenpos.domain;

import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class TableValidator {

    private final OrderRepository orderRepository;

    public TableValidator(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void validateToChangeEmpty(final OrderTable orderTable) {
        if (orderTable.getTableGroupId() != null) {
            throw new IllegalArgumentException("주문 테이블의 자리 상태를 변경할 때 주문 테이블이 테이블 그룹에 포함되어 있어서는 안됩니다.");
        }

        if (orderRepository.existsByOrderTableIdAndOrderStatusIn(
                orderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException("주문 테이블의 자리 상태를 변경할 때 주문 테이블의 주문 상태는 조리 또는 식사여서는 안됩니다.");
        }
    }

    public void validateToChangeNumberOfGuests(final OrderTable orderTable) {
        if (orderTable.isEmpty()) {
            throw new IllegalArgumentException("주문 테이블의 손님 수를 변경할 때 주문 테이블의 자리가 비어있어서는 안됩니다.");
        }
    }
}
