package kitchenpos.dao;

import static kitchenpos.support.fixture.TableGroupFixture.TABLE_GROUP_1;
import static kitchenpos.support.fixture.TableGroupFixture.TABLE_GROUP_2;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

@JdbcTest
class JdbcTemplateTableGroupDaoTest {

    private final JdbcTemplateTableGroupDao jdbcTemplateTableGroupDao;

    @Autowired
    private JdbcTemplateTableGroupDaoTest(final DataSource dataSource) {
        this.jdbcTemplateTableGroupDao = new JdbcTemplateTableGroupDao(dataSource);
    }

    @Test
    void 테이블_그룹을_저장한다() {
        // given
        TableGroup tableGroup = TABLE_GROUP_1.생성();

        // when
        TableGroup savedTableGroup = jdbcTemplateTableGroupDao.save(tableGroup);

        // then
        assertThat(tableGroup).isEqualToIgnoringGivenFields(savedTableGroup, "id");
    }

    @Test
    void 테이블_그룹을_ID_로_조회한다() {
        // given
        TableGroup savedTableGroup = 테이블_그룹_저장(TABLE_GROUP_1.생성());
        long id = savedTableGroup.getId();

        // when
        TableGroup selectedTableGroup = jdbcTemplateTableGroupDao.findById(id)
                .get();

        // then
        assertThat(savedTableGroup).isEqualToComparingFieldByField(selectedTableGroup);
    }

    @Test
    void 테이블_그룹_전체를_조회한다() {
        // given
        TableGroup savedTableGroup1 = 테이블_그룹_저장(TABLE_GROUP_1.생성());
        TableGroup savedTableGroup2 = 테이블_그룹_저장(TABLE_GROUP_2.생성());
        List<TableGroup> savedTableGroups = new ArrayList<>();
        savedTableGroups.add(savedTableGroup1);
        savedTableGroups.add(savedTableGroup2);

        // when
        List<TableGroup> selectedTableGroups = jdbcTemplateTableGroupDao.findAll();

        // then
        assertThat(savedTableGroups).usingRecursiveComparison()
                .isEqualTo(selectedTableGroups);
    }

    private TableGroup 테이블_그룹_저장(final TableGroup tableGroup) {
        return jdbcTemplateTableGroupDao.save(tableGroup);
    }
}
