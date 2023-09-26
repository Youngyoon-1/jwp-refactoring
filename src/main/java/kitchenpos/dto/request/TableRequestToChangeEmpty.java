package kitchenpos.dto.request;

public class TableRequestToChangeEmpty {

    private boolean empty;

    private TableRequestToChangeEmpty() {
    }

    public TableRequestToChangeEmpty(final boolean empty) {
        this.empty = empty;
    }

    public boolean isEmpty() {
        return this.empty;
    }
}
