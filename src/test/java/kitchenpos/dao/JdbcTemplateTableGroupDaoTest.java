package kitchenpos.dao;

import static kitchenpos.support.fixture.TableGroupFixture.TABLE_GROUP;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
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
        TableGroup tableGroup = TABLE_GROUP.생성(LocalDateTime.now());

        // when
        TableGroup savedTableGroup = jdbcTemplateTableGroupDao.save(tableGroup);

        // then
        assertThat(tableGroup).isEqualToIgnoringGivenFields(savedTableGroup, "id");
    }

    @Test
    void 테이블_그룹을_ID_로_조회한다() {
        // given
        TableGroup savedTableGroup = 테이블_그룹_저장(TABLE_GROUP.생성(LocalDateTime.now()));
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
        TableGroup savedTableGroup1 = 테이블_그룹_저장(TABLE_GROUP.생성(LocalDateTime.now()));
        TableGroup savedTableGroup2 = 테이블_그룹_저장(TABLE_GROUP.생성(LocalDateTime.now()));
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
