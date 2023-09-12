package kitchenpos.support.fixture;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;

public enum OrderTableFixture {

    ORDER_TABLE_1,
    ORDER_TABLE_2,
    ;

//    private final int numberOfGuests;
//    private final boolean empty;
//
//    OrderTableFixture(final int numberOfGuests, final boolean empty) {
//        this.numberOfGuests = numberOfGuests;
//        this.empty = empty;
//    }

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
}
