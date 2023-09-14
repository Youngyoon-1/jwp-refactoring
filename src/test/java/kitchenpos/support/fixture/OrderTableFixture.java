package kitchenpos.support.fixture;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;

public enum OrderTableFixture {

    ORDER_TABLE,
    ;

    public OrderTable 손님_한_명_테이블_그룹_없이_생성() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(1);
        orderTable.setEmpty(false);

        return orderTable;
    }

    public OrderTable 손님_한_명_테이블_그룹과_생성(final TableGroup tableGroup) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(1);
        orderTable.setTableGroupId(tableGroup.getId());
        orderTable.setEmpty(false);

        return orderTable;
    }

    public OrderTable 생성() {
        return 생성(null, null, null, null);
    }

    public OrderTable 생성(final boolean empty) {
        return 생성(null, null, empty, null);
    }

    public OrderTable 생성(final int numberOfGuests) {
        return 생성(null, null, null, numberOfGuests);
    }

    public OrderTable 생성(final long id, final long tableGroupId) {
        return 생성(id, tableGroupId, null, null);
    }

    public OrderTable 생성(final long tableGroupId) {
        return 생성(null, tableGroupId, null, null);
    }

    public OrderTable 생성(final long tableGroupId, final boolean empty) {
        return 생성(null, tableGroupId, empty, null);
    }

    private OrderTable 생성(final Long id, final Long tableGroupId, final Boolean empty, final Integer numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(tableGroupId);
        if (empty != null) {
            orderTable.setEmpty(empty);
        }
        if (numberOfGuests != null) {
            orderTable.setNumberOfGuests(numberOfGuests);
        }
        return orderTable;
    }
}
