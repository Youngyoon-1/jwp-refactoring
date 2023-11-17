package kitchenpos.dto.response;

import kitchenpos.domain.OrderTable;

public class OrderTableResponse {

    private long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    private OrderTableResponse() {
    }

    public OrderTableResponse(final OrderTable orderTable) {
        this.id = orderTable.getId();
        this.tableGroupId = orderTable.getTableGroupId();
        this.numberOfGuests = orderTable.getNumberOfGuests();
        this.empty = orderTable.isEmpty();
    }

    public long getId() {
        return this.id;
    }

    public Long getTableGroupId() {
        return this.tableGroupId;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public int getNumberOfGuests() {
        return this.numberOfGuests;
    }
}
