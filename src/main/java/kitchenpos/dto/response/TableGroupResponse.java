package kitchenpos.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.TableGroup;

public class TableGroupResponse {

    private Long id;
    private LocalDateTime createdDate;
    private List<OrderTableResponse> orderTables;

    private TableGroupResponse() {
    }

    public TableGroupResponse(final TableGroup tableGroup) {
        this.id = tableGroup.getId();
        this.createdDate = tableGroup.getCreatedDate();
        this.orderTables = tableGroup.getOrderTables()
                .stream()
                .map(OrderTableResponse::new)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return this.id;
    }

    public LocalDateTime getCreatedDate() {
        return this.createdDate;
    }

    public List<OrderTableResponse> getOrderTables() {
        return this.orderTables;
    }
}
