package kitchenpos.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TableGroupValidator {

    private final OrderRepository orderRepository;

    public TableGroupValidator(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void validateToCreate(final TableGroup tableGroup) {
        final List<OrderTable> orderTables = tableGroup.getOrderTables();
        if (orderTables == null || orderTables.size() < 2) {
            throw new IllegalArgumentException("테이블 그룹 생성시 주문 테이블 개수는 2개 이상이어야 합니다.");
        }
    }

    public void validateToCreate(final List<OrderTable> orderTables, final List<OrderTable> savedOrderTables) {
        if (orderTables.size() != savedOrderTables.size()) {
            throw new IllegalArgumentException("테이블 그룹 생성시 주문 테이블이 저장되어 있어야 합니다.");
        }

        for (final OrderTable orderTable : savedOrderTables) {
            validateToCreate(orderTable);
        }
    }

    private void validateToCreate(final OrderTable orderTable) {
        if (!orderTable.isEmpty()) {
            throw new IllegalArgumentException("테이블 그룹 생성시 주문 테이블이 비어있어야 합니다.");
        }
        if (orderTable.getTableGroupId() != null) {
            throw new IllegalArgumentException("테이블 그룹 생성시 주문 테이블의 테이블 그룹이 이미 존재해서는 안됩니다.");
        }
    }

    public void validateToUngroup(final List<OrderTable> orderTables) {
        final List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        if (orderRepository.existsByOrderTableIdInAndOrderStatusIn(
                orderTableIds, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException("테이블 그룹 해제시 주문 테이블의 주문 상태가 식사 또는 조리면 안됩니다.");
        }
    }
}
