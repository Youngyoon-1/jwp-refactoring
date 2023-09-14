package kitchenpos.support.fixture;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;

public enum TableGroupFixture {

    TABLE_GROUP,
    ;

    public TableGroup 생성() {
        return 생성(null, null, LocalDateTime.now());
    }

    public TableGroup 생성(final long id) {
        return 생성(id, null, null);

    }


    public TableGroup 생성(final List<OrderTable> orderTables) {
        return 생성(null, orderTables, null);
    }

    public TableGroup 생성(final Long id, final List<OrderTable> orderTables, final LocalDateTime createdDate) {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(id);
        tableGroup.setCreatedDate(createdDate);
        tableGroup.setOrderTables(orderTables);

        return tableGroup;
    }
}
