package kitchenpos.dao;

import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_2;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

@JdbcTest
class JdbcTemplateMenuGroupDaoTest {

    private final JdbcTemplateMenuGroupDao jdbcTemplateMenuGroupDao;

    @Autowired
    private JdbcTemplateMenuGroupDaoTest(final DataSource datasource) {
        this.jdbcTemplateMenuGroupDao = new JdbcTemplateMenuGroupDao(datasource);
    }

    @Test
    void 메뉴_그룹을_저장한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성();

        // when
        MenuGroup savedMenuGroup = jdbcTemplateMenuGroupDao.save(menuGroup);

        // then
        assertThat(menuGroup).isEqualToIgnoringGivenFields(savedMenuGroup, "id");
    }

    @Test
    void 메뉴_ID_로_메뉴_그룹을_조회한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        long id = savedMenuGroup.getId();

        // when
        MenuGroup selectedMenuGroup = jdbcTemplateMenuGroupDao.findById(id)
                .get();

        // then
        assertThat(savedMenuGroup).isEqualToComparingFieldByField(selectedMenuGroup);
    }

    @Test
    void 메뉴_그룹_전체를_조회한다() {
        // given
        MenuGroup savedMenuGroup1 = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        MenuGroup savedMenuGroup2 = 메뉴_그룹_저장(MENU_GROUP_2.생성());
        List<MenuGroup> savedMenuGroups = new ArrayList<>();
        savedMenuGroups.add(savedMenuGroup1);
        savedMenuGroups.add(savedMenuGroup2);

        // when
        List<MenuGroup> selectedMenuGroups = jdbcTemplateMenuGroupDao.findAll();

        // then
        assertThat(savedMenuGroups).usingRecursiveComparison()
                .isEqualTo(selectedMenuGroups);
    }

    @Test
    void ID_로_메뉴_그룹이_존재_여부를_조회한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        long id = savedMenuGroup.getId();

        // when
        boolean result = jdbcTemplateMenuGroupDao.existsById(id);

        // then
        assertThat(result).isTrue();
    }

    private MenuGroup 메뉴_그룹_저장(final MenuGroup menuGroup) {
        return jdbcTemplateMenuGroupDao.save(menuGroup);
    }
}