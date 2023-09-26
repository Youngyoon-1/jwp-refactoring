package kitchenpos.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TableGroup {
    private Long id;
    private LocalDateTime createdDate;
    private List<OrderTable> orderTables;

    public TableGroup() {
    }

    public TableGroup(final Long id, final LocalDateTime createdDate, final List<OrderTable> orderTables) {
        this.id = id;
        this.createdDate = createdDate;
        this.orderTables = orderTables;
    }

    public static TableGroup createToSave(final List<OrderTable> orderTables) {
        if (orderTables == null || orderTables.size() < 2) {
            throw new IllegalArgumentException();
        }
        return new TableGroup(null, LocalDateTime.now(), orderTables);
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<OrderTable> getOrderTables() {
        return orderTables;
    }

    public void setOrderTables(final List<OrderTable> orderTables) {
        this.orderTables = orderTables;
    }

    public List<Long> collectOrderTableIds() {
        return orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());
    }

    public void validateToSave(final List<OrderTable> orderTables) {
        if (this.orderTables.size() != orderTables.size()) {
            throw new IllegalArgumentException();
        }
    }
}
