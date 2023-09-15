package kitchenpos.application;

import static kitchenpos.support.fixture.MenuFixture.MENU_1;
import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static kitchenpos.support.fixture.MenuProductFixture.MENU_PRODUCT;
import static kitchenpos.support.fixture.ProductFixture.PRODUCT_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuService menuService;

    @Test
    void 메뉴를_저장한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성(1L);
        Product product = PRODUCT_1.생성(1L, BigDecimal.valueOf(500));
        MenuProduct menuProductRequest = MENU_PRODUCT.생성(product, 2);
        List<MenuProduct> menuProductsRequest = new ArrayList<>();
        menuProductsRequest.add(menuProductRequest);
        Menu menuRequest = MENU_1.생성(menuGroup, menuProductsRequest);
        Menu menu = MENU_1.생성(1L, menuGroup);
        MenuProduct menuProduct = MENU_PRODUCT.생성(1L, menu, product);
        given(menuGroupDao.existsById(1L))
                .willReturn(true);
        given(productDao.findById(1L))
                .willReturn(Optional.of(product));
        given(menuDao.save(menuRequest))
                .willReturn(menu);
        given(menuProductDao.save(menuProductRequest))
                .willReturn(menuProduct);

        // when
        Menu createdMenu = menuService.create(menuRequest);

        // then
        MenuProduct createdMenuProduct = createdMenu.getMenuProducts()
                .get(0);
        long menuId = menuProductRequest.getMenuId();
        assertAll(
                () -> assertThat(createdMenuProduct).isEqualToComparingFieldByField(menuProduct),
                () -> assertThat(menuId).isEqualTo(1L),
                () -> verify(menuGroupDao).existsById(1L),
                () -> verify(productDao).findById(1L),
                () -> verify(menuDao).save(menuRequest),
                () -> verify(menuProductDao).save(menuProductRequest)
        );
    }

    @ParameterizedTest
    @CsvSource({"-1"})
    @NullSource
    void 메뉴를_생성할_때_메뉴_가격이_null_또는_0_원_미만이면_예외가_발생한다(final BigDecimal invalidPrice) {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성(1L);
        Menu menuRequest = MENU_1.생성(menuGroup, invalidPrice);

        // when, then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_생성할_때_저장되지_않은_메뉴_그룹인_경우_예외가_발생한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성(1L);
        Menu menuRequest = MENU_1.생성(menuGroup);
        given(menuGroupDao.existsById(1L))
                .willReturn(false);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> menuService.create(menuRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(menuGroupDao).existsById(1L)
        );
    }

    @Test
    void 메뉴를_생성할_때_저장되지_않은_제품이_포함된_경우_예외가_발생한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성(1L);
        Product product = PRODUCT_1.생성(1L);
        MenuProduct menuProductRequest = MENU_PRODUCT.생성(product);
        List<MenuProduct> menuProductsRequest = new ArrayList<>();
        menuProductsRequest.add(menuProductRequest);
        Menu menuRequest = MENU_1.생성(menuGroup, menuProductsRequest);
        given(menuGroupDao.existsById(1L))
                .willReturn(true);
        given(productDao.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> menuService.create(menuRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(menuGroupDao).existsById(1L),
                () -> verify(productDao).findById(1L)
        );
    }

    @Test
    void 메뉴를_생성할_때_메뉴_가격이_제품_가격의_합보다_큰_경우_예외가_발생한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성(1L);
        Product product = PRODUCT_1.생성(1L, BigDecimal.valueOf(999));
        MenuProduct menuProductRequest = MENU_PRODUCT.생성(product);
        List<MenuProduct> menuProductsRequest = new ArrayList<>();
        menuProductsRequest.add(menuProductRequest);
        // 1000원짜리 메뉴 생성
        Menu menuRequest = MENU_1.생성(menuGroup, menuProductsRequest);
        given(menuGroupDao.existsById(1L))
                .willReturn(true);
        given(productDao.findById(1L))
                .willReturn(Optional.of(product));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> menuService.create(menuRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(menuGroupDao).existsById(1L),
                () -> verify(productDao).findById(1L)
        );
    }

    @Test
    void 메뉴_전체를_조회한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성(1L);
        Menu menu = MENU_1.생성(1L, menuGroup);
        List<Menu> menus = new ArrayList<>();
        menus.add(menu);
        Product product = PRODUCT_1.생성(1L);
        MenuProduct menuProduct = MENU_PRODUCT.생성(1L, menu, product);
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(menuProduct);
        given(menuDao.findAll())
                .willReturn(menus);
        given(menuProductDao.findAllByMenuId(1L))
                .willReturn(menuProducts);

        // when
        List<Menu> selectedMenus = menuService.list();

        // then
        List<MenuProduct> selectedMenuProducts = selectedMenus.get(0)
                .getMenuProducts();
        assertAll(
                () -> assertThat(menuProducts).usingRecursiveComparison()
                        .isEqualTo(selectedMenuProducts),
                () -> verify(menuDao).findAll(),
                () -> verify(menuProductDao).findAllByMenuId(1L)
        );
    }
}
