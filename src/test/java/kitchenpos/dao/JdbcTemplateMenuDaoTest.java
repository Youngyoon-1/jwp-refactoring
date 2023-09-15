package kitchenpos.dao;

import static kitchenpos.support.fixture.MenuFixture.MENU_1;
import static kitchenpos.support.fixture.MenuFixture.MENU_2;
import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

@JdbcTest
class JdbcTemplateMenuDaoTest {

    private final JdbcTemplateMenuDao jdbcTemplateMenuDao;
    private final JdbcTemplateMenuGroupDao jdbcTemplateMenuGroupDao;

    @Autowired
    private JdbcTemplateMenuDaoTest(final DataSource dataSource) {
        this.jdbcTemplateMenuDao = new JdbcTemplateMenuDao(dataSource);
        this.jdbcTemplateMenuGroupDao = new JdbcTemplateMenuGroupDao(dataSource);
    }

    @Test
    void 메뉴를_저장한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu menu = MENU_1.생성(savedMenuGroup);

        // when
        Menu savedMenu = jdbcTemplateMenuDao.save(menu);

        // then
        BigDecimal menuPrice = menu.getPrice();
        BigDecimal savedMenuPrice = savedMenu.getPrice();
        assertAll(
                () -> assertThat(menu).isEqualToIgnoringGivenFields(savedMenu, "id", "price"),
                () -> assertThat(menuPrice).isEqualByComparingTo(savedMenuPrice)
        );
    }

    @Test
    void 메뉴_ID_로_메뉴를_조회한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu = 메뉴_저장(MENU_1.생성(savedMenuGroup));

        // when
        Menu selectedMenu = jdbcTemplateMenuDao.findById(savedMenu.getId())
                .get();

        // then
        assertThat(savedMenu).isEqualToComparingFieldByField(selectedMenu);
    }

    @Test
    void 메뉴_전체를_조회한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu1 = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        Menu savedMenu2 = 메뉴_저장(MENU_2.생성(savedMenuGroup));
        List<Menu> savedMenus = new ArrayList<>();
        savedMenus.add(savedMenu1);
        savedMenus.add(savedMenu2);

        // when
        List<Menu> selectedMenus = jdbcTemplateMenuDao.findAll();

        // then
        assertThat(savedMenus).usingRecursiveComparison()
                .isEqualTo(selectedMenus);
    }

    @Test
    void N_개_이상의_ID_로_메뉴_개수를_조회한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu1 = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        Menu savedMenu2 = 메뉴_저장(MENU_2.생성(savedMenuGroup));
        List<Long> ids = new ArrayList<>();
        ids.add(savedMenu1.getId());
        ids.add(savedMenu2.getId());

        // when
        long count = jdbcTemplateMenuDao.countByIdIn(ids);

        // then
        assertThat(ids.size()).isEqualTo(count);
    }

    private MenuGroup 메뉴_그룹_저장(final MenuGroup menuGroup) {
        return jdbcTemplateMenuGroupDao.save(menuGroup);
    }

    private Menu 메뉴_저장(final Menu menu) {
        return jdbcTemplateMenuDao.save(menu);
    }
}
