package kitchenpos.dto.request;

import kitchenpos.domain.OrderTable;

public class TableRequestToCreate {

    private int numberOfGuests;
    private boolean empty;

    private TableRequestToCreate() {
    }

    public TableRequestToCreate(final int numberOfGuests, final boolean empty) {
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public OrderTable toEntity() {
        return OrderTable.createToSave(this.numberOfGuests, this.empty);
    }

    public int getNumberOfGuests() {
        return this.numberOfGuests;
    }

    public boolean isEmpty() {
        return this.empty;
    }
}
