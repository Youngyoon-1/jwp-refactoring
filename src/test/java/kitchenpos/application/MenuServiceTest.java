package kitchenpos.application;

import static kitchenpos.support.fixture.MenuProductFixture.MENU_PRODUCT;
import static kitchenpos.support.fixture.ProductFixture.PRODUCT_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.dto.request.MenuRequest;
import kitchenpos.dto.response.MenuProductResponse;
import kitchenpos.dto.response.MenuResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentMatchers;
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
        Product product = PRODUCT_1.생성(1L, BigDecimal.valueOf(500));
        MenuProduct menuProduct = MENU_PRODUCT.생성(product, 2);
        List<MenuProduct> menuProducts = Collections.singletonList(menuProduct);
        MenuRequest menuRequest = new MenuRequest("메뉴", BigDecimal.valueOf(1000), 1L, menuProducts);
        MenuProduct willReturnMenuProduct = new MenuProduct(1L, 1L, 1L, 1L);
        List<MenuProduct> willReturnMenuProducts = Collections.singletonList(willReturnMenuProduct);
        Menu willReturnMenu = new Menu(1L, "메뉴", BigDecimal.valueOf(1000), 1L, willReturnMenuProducts);
        given(menuGroupDao.existsById(1L))
                .willReturn(true);
        given(productDao.findById(1L))
                .willReturn(Optional.of(product));
        given(menuDao.save(ArgumentMatchers.any(Menu.class)))
                .willReturn(willReturnMenu);
        given(menuProductDao.save(ArgumentMatchers.any(MenuProduct.class)))
                .willReturn(willReturnMenuProduct);

        // when
        MenuResponse createdMenu = menuService.create(menuRequest);

        // then
        long actualMenuId = willReturnMenu.getId();
        MenuProductResponse createdMenuProduct = createdMenu.getMenuProducts()
                .get(0);
        long expectationMenuId = createdMenuProduct.getMenuId();
        assertAll(
                () -> assertThat(actualMenuId).isEqualTo(expectationMenuId),
                () -> verify(menuGroupDao).existsById(1L),
                () -> verify(productDao).findById(1L),
                () -> verify(menuDao).save(ArgumentMatchers.any(Menu.class)),
                () -> verify(menuProductDao).save(ArgumentMatchers.any(MenuProduct.class))
        );
    }

    @ParameterizedTest
    @CsvSource({"-1"})
    @NullSource
    void 메뉴를_생성할_때_메뉴_가격이_null_또는_0_원_미만이면_예외가_발생한다(final BigDecimal invalidPrice) {
        // given
        MenuRequest menuRequest = new MenuRequest("메뉴", invalidPrice, 1L, new ArrayList<>());

        // when, then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_생성할_때_저장되지_않은_메뉴_그룹인_경우_예외가_발생한다() {
        // given
        MenuRequest menuRequest = new MenuRequest("메뉴", BigDecimal.ZERO, 1L, new ArrayList<>());
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
        Product product = PRODUCT_1.생성(1L);
        MenuProduct menuProduct = MENU_PRODUCT.생성(product);
        List<MenuProduct> menuProducts = Collections.singletonList(menuProduct);
        MenuRequest menuRequest = new MenuRequest("메뉴", BigDecimal.ZERO, 1L, menuProducts);
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
        Product product = PRODUCT_1.생성(1L, BigDecimal.ZERO);
        MenuProduct menuProduct = MENU_PRODUCT.생성(product);
        List<MenuProduct> menuProducts = Collections.singletonList(menuProduct);
        MenuRequest menuRequest = new MenuRequest("메뉴", BigDecimal.ONE, 1L, menuProducts);
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
        List<Menu> menus = Collections.singletonList(
                new Menu(1L, "메뉴", BigDecimal.valueOf(1000), 1L, new ArrayList<>())
        );
        MenuProduct menuProduct = new MenuProduct(1L, 1L, 1L, 1L);
        List<MenuProduct> menuProducts = Collections.singletonList(menuProduct);
        given(menuDao.findAll())
                .willReturn(menus);
        given(menuProductDao.findAllByMenuId(1L))
                .willReturn(menuProducts);

        // when
        List<MenuResponse> response = menuService.list();

        // then
        List<MenuProductResponse> menuProductResponses = response.get(0)
                .getMenuProducts();
        assertAll(
                () -> assertThat(menuProductResponses).isNotEmpty(),
                () -> verify(menuDao).findAll(),
                () -> verify(menuProductDao).findAllByMenuId(1L)
        );
    }
}
