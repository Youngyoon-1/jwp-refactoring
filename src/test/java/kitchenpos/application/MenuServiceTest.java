package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuProductRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.MenuValidator;
import kitchenpos.dto.request.MenuRequest;
import kitchenpos.dto.response.MenuProductResponse;
import kitchenpos.dto.response.MenuResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuProductRepository menuProductDao;

    @Mock
    private MenuValidator menuValidator;

    @InjectMocks
    private MenuService menuService;

    @Test
    void 메뉴를_저장한다() {
        // given
        MenuProduct savedMenuProduct = new MenuProduct(1L, 1L, 1L, 1L);
        Menu savedMenu = new Menu(1L, "메뉴이름", BigDecimal.valueOf(1000), 1L,
                Collections.singletonList(savedMenuProduct));
        BDDMockito.willDoNothing().given(menuValidator)
                .validate(ArgumentMatchers.any(Menu.class));
        given(menuRepository.save(ArgumentMatchers.any(Menu.class)))
                .willReturn(savedMenu);
        given(menuProductDao.save(ArgumentMatchers.any(MenuProduct.class)))
                .willReturn(savedMenuProduct);

        // when
        MenuProduct menuProduct = new MenuProduct(null, null, 1L, 1L);
        MenuRequest menuRequest = new MenuRequest("메뉴이름", BigDecimal.valueOf(1000), 1L,
                Collections.singletonList(menuProduct));
        MenuResponse menuResponse = menuService.create(menuRequest);

        // then
        long menuId = menuResponse.getId();
        String name = menuResponse.getName();
        BigDecimal price = menuResponse.getPrice();
        long menuGroupId = menuResponse.getMenuGroupId();
        MenuProductResponse menuProductResponse = menuResponse.getMenuProducts()
                .get(0);
        long menuProductId = menuProductResponse.getSeq();
        long menuIdOfMenuProduct = menuProductResponse.getMenuId();
        long productId = menuProductResponse.getProductId();
        long quantity = menuProductResponse.getQuantity();
        assertAll(
                () -> assertThat(menuId).isEqualTo(1L),
                () -> assertThat(name).isEqualTo("메뉴이름"),
                () -> assertThat(price).isEqualTo(BigDecimal.valueOf(1000)),
                () -> assertThat(menuGroupId).isEqualTo(1L),
                () -> assertThat(menuProductId).isEqualTo(1L),
                () -> assertThat(menuIdOfMenuProduct).isEqualTo(1L),
                () -> assertThat(productId).isEqualTo(1L),
                () -> assertThat(quantity).isEqualTo(1L),
                () -> verify(menuValidator).validate(ArgumentMatchers.any(Menu.class)),
                () -> verify(menuRepository).save(ArgumentMatchers.any(Menu.class)),
                () -> verify(menuProductDao).save(ArgumentMatchers.any(MenuProduct.class))
        );
    }

    @Test
    void 메뉴_전체를_조회한다() {
        // given
        MenuProduct menuProduct = new MenuProduct(1L, 1L, 1L, 1L);
        List<MenuProduct> menuProducts = Collections.singletonList(menuProduct);
        List<Menu> menus = Collections.singletonList(
                new Menu(1L, "메뉴이름", BigDecimal.valueOf(1000), 1L, menuProducts)
        );
        given(menuRepository.findAll())
                .willReturn(menus);

        // when
        List<MenuResponse> response = menuService.list();

        // then
        MenuResponse menuResponse = response.get(0);
        long menuId = menuResponse.getId();
        String name = menuResponse.getName();
        BigDecimal price = menuResponse.getPrice();
        long menuGroupId = menuResponse.getMenuGroupId();
        MenuProductResponse menuProductResponse = menuResponse.getMenuProducts()
                .get(0);
        long menuProductId = menuProductResponse.getSeq();
        long menuIdOfMenuProduct = menuProductResponse.getMenuId();
        long productId = menuProductResponse.getProductId();
        long quantity = menuProductResponse.getQuantity();
        assertAll(
                () -> assertThat(menuId).isEqualTo(1L),
                () -> assertThat(name).isEqualTo("메뉴이름"),
                () -> assertThat(price).isEqualTo(BigDecimal.valueOf(1000)),
                () -> assertThat(menuGroupId).isEqualTo(1L),
                () -> assertThat(menuProductId).isEqualTo(1L),
                () -> assertThat(menuIdOfMenuProduct).isEqualTo(1L),
                () -> assertThat(productId).isEqualTo(1L),
                () -> assertThat(quantity).isEqualTo(1L),
                () -> verify(menuRepository).findAll()
        );
    }
}
