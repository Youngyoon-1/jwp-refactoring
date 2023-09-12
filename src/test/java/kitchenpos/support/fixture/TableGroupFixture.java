package kitchenpos.support.fixture;

import java.time.LocalDateTime;
import kitchenpos.domain.TableGroup;

public enum TableGroupFixture {

    TABLE_GROUP_1,
    TABLE_GROUP_2,
    ;

    public TableGroup 생성() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(LocalDateTime.now());

        return tableGroup;
    }
}
