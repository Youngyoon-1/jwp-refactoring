package kitchenpos.domain;

import java.util.List;
import java.util.stream.Collectors;

public class OrderTables {

    private List<OrderTable> orderTables;

    private OrderTables(final List<OrderTable> orderTables) {
        this.orderTables = orderTables;
    }

    public static OrderTables createToSaveTableGroup(final List<OrderTable> orderTables) {
        final boolean anyMatching = orderTables.stream()
                .anyMatch(
                        orderTable -> !orderTable.isEmpty() || orderTable.getTableGroupId() != null
                );
        if (anyMatching) {
            throw new IllegalArgumentException();
        }
        orderTables.forEach(orderTable -> orderTable.setEmpty(false));
        return new OrderTables(orderTables);
    }

    public static OrderTables createToUngroup(final List<OrderTable> orderTables) {
        orderTables.forEach(orderTable -> orderTable.setTableGroupId(null));
        orderTables.forEach(orderTable -> orderTable.setEmpty(false));
        return new OrderTables(orderTables);
    }

    public List<OrderTable> getOrderTables() {
        return this.orderTables;
    }

    public List<Long> collectIds() {
        return this.orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());
    }
}
