package kitchenpos.dto.request;

public class TableRequestToCreateTableGroup {

    private Long id;

    private TableRequestToCreateTableGroup() {
    }

    public TableRequestToCreateTableGroup(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
