package kitchenpos.domain;

public class OrderTable {

    private Long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    public OrderTable() {
    }

    public OrderTable(final Long id, final Long tableGroupId, final int numberOfGuests, final boolean empty) {
        this.id = id;
        this.tableGroupId = tableGroupId;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public static OrderTable createToSave(final int numberOfGuests, final boolean empty) {
        return new OrderTable(null, null, numberOfGuests, empty);
    }

    public static OrderTable createToUpdateNumberOfGuests(final int numberOfGuests) {
        if (numberOfGuests < 0) {
            throw new IllegalArgumentException();
        }
        return new OrderTable(null, null, numberOfGuests, false);
    }

    public static OrderTable createToSaveTableGroup(final Long id) {
        final OrderTable orderTable = new OrderTable();
        orderTable.id = id;
        return orderTable;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getTableGroupId() {
        return tableGroupId;
    }

    public void setTableGroupId(final Long tableGroupId) {
        this.tableGroupId = tableGroupId;
    }

    public int getNumberOfGuests() {
        return this.numberOfGuests;
    }

    public void setNumberOfGuests(final int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(final boolean empty) {
        this.empty = empty;
    }

    public void validateToUpdateEmpty() {
        if (this.tableGroupId != null) {
            throw new IllegalArgumentException();
        }
    }

    public void updateNumberOfGuests(final OrderTable other) {
        if (this.empty) {
            throw new IllegalArgumentException();
        }
        this.numberOfGuests = other.getNumberOfGuests();
    }

    public void validateToSaveOrder() {
        if (this.empty) {
            throw new IllegalArgumentException();
        }
    }

    public void updateTableGroupId(final TableGroup tableGroup) {
        this.tableGroupId = tableGroup.getId();
    }
}
