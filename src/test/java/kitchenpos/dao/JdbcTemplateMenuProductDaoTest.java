package kitchenpos.dao;

import static kitchenpos.support.fixture.MenuFixture.MENU_1;
import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static kitchenpos.support.fixture.MenuProductFixture.MENU_PRODUCT;
import static kitchenpos.support.fixture.ProductFixture.PRODUCT_1;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.support.fixture.MenuProductFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

@JdbcTest
class JdbcTemplateMenuProductDaoTest {

    private final JdbcTemplateMenuProductDao jdbcTemplateMenuProductDao;
    private final JdbcTemplateMenuGroupDao jdbcTemplateMenuGroupDao;
    private final JdbcTemplateMenuDao jdbcTemplateMenuDao;
    private final JdbcTemplateProductDao jdbcTemplateProductDao;

    @Autowired
    private JdbcTemplateMenuProductDaoTest(final DataSource dataSource) {
        this.jdbcTemplateMenuProductDao = new JdbcTemplateMenuProductDao(dataSource);
        this.jdbcTemplateMenuGroupDao = new JdbcTemplateMenuGroupDao(dataSource);
        this.jdbcTemplateMenuDao = new JdbcTemplateMenuDao(dataSource);
        this.jdbcTemplateProductDao = new JdbcTemplateProductDao(dataSource);
    }

    @Test
    void 메뉴_제품을_저장한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        Product savedProduct = 제품_저장(PRODUCT_1.생성());
        MenuProduct menuProduct = MENU_PRODUCT.생성(savedMenu, savedProduct);

        // when
        MenuProduct savedMenuProduct = jdbcTemplateMenuProductDao.save(menuProduct);

        // then
        assertThat(menuProduct).isEqualToIgnoringGivenFields(savedMenuProduct, "seq");
    }

    @Test
    void ID_로_메뉴_제품을_조회한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        Product savedProduct = 제품_저장(PRODUCT_1.생성());
        MenuProduct savedMenuProduct = 메뉴_제품_저장(MENU_PRODUCT.생성(savedMenu, savedProduct));
        long id = savedMenuProduct.getSeq();

        // when
        MenuProduct selectedMenuProduct = jdbcTemplateMenuProductDao.findById(id)
                .get();

        // then
        assertThat(savedMenuProduct).isEqualToComparingFieldByField(selectedMenuProduct);
    }

    @Test
    void 메뉴_제품_전체를_조회한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        Product savedProduct = 제품_저장(PRODUCT_1.생성());
        MenuProduct savedMenuProduct1 = 메뉴_제품_저장(MENU_PRODUCT.생성(savedMenu, savedProduct));
        MenuProduct savedMenuProduct2 = 메뉴_제품_저장(MenuProductFixture.MENU_PRODUCT.생성(savedMenu, savedProduct));
        List<MenuProduct> savedMenuProducts = new ArrayList<>();
        savedMenuProducts.add(savedMenuProduct1);
        savedMenuProducts.add(savedMenuProduct2);

        // when
        List<MenuProduct> selectedMenuProducts = jdbcTemplateMenuProductDao.findAll();

        // then
        assertThat(savedMenuProducts).usingRecursiveComparison()
                .isEqualTo(selectedMenuProducts);
    }

    @Test
    void 메뉴_ID_로_메뉴_상품들을_조회한다() {
        // given
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        Product savedProduct = 제품_저장(PRODUCT_1.생성());
        MenuProduct savedMenuProduct1 = 메뉴_제품_저장(MENU_PRODUCT.생성(savedMenu, savedProduct));
        MenuProduct savedMenuProduct2 = 메뉴_제품_저장(MenuProductFixture.MENU_PRODUCT.생성(savedMenu, savedProduct));
        List<MenuProduct> savedMenuProducts = new ArrayList<>();
        savedMenuProducts.add(savedMenuProduct1);
        savedMenuProducts.add(savedMenuProduct2);
        long menuId = savedMenu.getId();

        // when
        List<MenuProduct> selectedMenuProducts = jdbcTemplateMenuProductDao.findAllByMenuId(menuId);

        // then
        assertThat(savedMenuProducts).usingRecursiveComparison()
                .isEqualTo(selectedMenuProducts);
    }

    private MenuProduct 메뉴_제품_저장(final MenuProduct menuProduct) {
        return jdbcTemplateMenuProductDao.save(menuProduct);
    }

    private Product 제품_저장(final Product product) {
        return jdbcTemplateProductDao.save(product);
    }

    private Menu 메뉴_저장(final Menu menu) {
        return jdbcTemplateMenuDao.save(menu);
    }

    private MenuGroup 메뉴_그룹_저장(final MenuGroup menuGroup) {
        return jdbcTemplateMenuGroupDao.save(menuGroup);
    }
}
