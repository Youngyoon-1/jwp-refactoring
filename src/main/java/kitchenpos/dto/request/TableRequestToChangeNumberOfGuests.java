package kitchenpos.dto.request;

import kitchenpos.domain.OrderTable;

public class TableRequestToChangeNumberOfGuests {

    private int numberOfGuests;

    private TableRequestToChangeNumberOfGuests() {
    }

    public TableRequestToChangeNumberOfGuests(final int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public int getNumberOfGuests() {
        return this.numberOfGuests;
    }

    public OrderTable toEntity() {
        return OrderTable.createToUpdateNumberOfGuests(this.numberOfGuests);
    }
}
