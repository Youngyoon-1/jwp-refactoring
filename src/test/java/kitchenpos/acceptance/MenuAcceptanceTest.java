package kitchenpos.acceptance;

import static kitchenpos.support.fixture.MenuFixture.MENU_1;
import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static kitchenpos.support.fixture.MenuProductFixture.MENU_PRODUCT;
import static kitchenpos.support.fixture.ProductFixture.PRODUCT_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import kitchenpos.dao.JdbcTemplateMenuGroupDao;
import kitchenpos.dao.JdbcTemplateProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@AcceptanceTest
public class MenuAcceptanceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplateMenuGroupDao jdbcTemplateMenuGroupDao;

    @Autowired
    private JdbcTemplateProductDao jdbcTemplateProductDao;

    @Test
    void 메뉴를_한_개_등록한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성();
        MenuGroup savedMenuGroup = jdbcTemplateMenuGroupDao.save(menuGroup);
        Product product = PRODUCT_1.생성();
        Product savedProduct = jdbcTemplateProductDao.save(product);
        MenuProduct menuProduct = MENU_PRODUCT.생성(savedProduct);
        List<MenuProduct> menuProducts = Collections.singletonList(menuProduct);
        Menu request = MENU_1.생성(savedMenuGroup, menuProducts);

        // when
        ResponseEntity<Menu> response = testRestTemplate.postForEntity(
                "/api/menus",
                request,
                Menu.class
        );

        // then
        Menu actualBody = request;
        BigDecimal actualPrice = actualBody.getPrice();
        List<MenuProduct> actualMenuProducts = actualBody.getMenuProducts();
        Menu body = response.getBody();
        assert body != null;
        BigDecimal price = body.getPrice();
        List<MenuProduct> menuProductsOfResponse = body.getMenuProducts();
        assertAll(
                () -> assertThat(actualBody).isEqualToIgnoringGivenFields(body, "id", "menuProducts", "price"),
                () -> assertThat(actualPrice).isCloseTo(price, withinPercentage(0.1)),
                () -> assertThat(actualMenuProducts).usingRecursiveComparison()
                        .ignoringFields("seq", "menuId")
                        .isEqualTo(menuProductsOfResponse)
        );
    }

    @Test
    void 메뉴_전체를_조회한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성();
        MenuGroup savedMenuGroup = jdbcTemplateMenuGroupDao.save(menuGroup);
        Product product = PRODUCT_1.생성();
        Product savedProduct = jdbcTemplateProductDao.save(product);
        MenuProduct menuProduct = MENU_PRODUCT.생성(savedProduct);
        List<MenuProduct> menuProducts = Collections.singletonList(menuProduct);
        Menu request = MENU_1.생성(savedMenuGroup, menuProducts);
        ResponseEntity<Menu> savedMenu = testRestTemplate.postForEntity(
                "/api/menus",
                request,
                Menu.class
        );

        // when
        ResponseEntity<List<Menu>> response = testRestTemplate.exchange(
                "/api/menus",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Menu>>() {
                }
        );

        // then
        Menu actualMenu = savedMenu.getBody();
        Menu selectedMenu = Objects.requireNonNull(response.getBody())
                .get(0);
        assert actualMenu != null;
        List<MenuProduct> actualMenuProducts = actualMenu.getMenuProducts();
        List<MenuProduct> selectedMenuProducts = selectedMenu.getMenuProducts();

        assertAll(
                () -> assertThat(actualMenu).isEqualToIgnoringGivenFields(selectedMenu, "menuProducts"),
                () -> assertThat(actualMenuProducts).usingRecursiveComparison()
                        .isEqualTo(selectedMenuProducts)
        );
    }
}
