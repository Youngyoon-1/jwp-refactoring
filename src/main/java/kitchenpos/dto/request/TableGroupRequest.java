package kitchenpos.dto.request;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;

public class TableGroupRequest {

    private List<TableRequestToCreateTableGroup> orderTables;

    private TableGroupRequest() {
    }

    public TableGroupRequest(final List<TableRequestToCreateTableGroup> orderTables) {
        this.orderTables = orderTables;
    }

    public TableGroup toEntity() {
        final List<OrderTable> orderTables = this.orderTables.stream()
                .map(tableRequest -> new OrderTable(tableRequest.getId()))
                .collect(Collectors.toList());
        return new TableGroup(orderTables);
    }

    public List<TableRequestToCreateTableGroup> getOrderTables() {
        return this.orderTables;
    }
}
