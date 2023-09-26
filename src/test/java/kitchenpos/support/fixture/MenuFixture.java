package kitchenpos.support.fixture;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public enum MenuFixture {

    MENU_1("메뉴1", BigDecimal.valueOf(1000)),
    MENU_2("메뉴2", BigDecimal.valueOf(1000)),
    ;

    private final String name;
    private final BigDecimal price;

    MenuFixture(final String name, final BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public Menu 생성() {
        return 생성(null, null, null, null);
    }

    public Menu 생성(final Long id) {
        return 생성(id, null, null, null);
    }

    public Menu 생성(final MenuGroup menuGroup) {
        return 생성(null, menuGroup, this.price, null);
    }

    public Menu 생성(final Long id, final MenuGroup menuGroup) {
        return 생성(id, menuGroup, this.price, null);
    }

    public Menu 생성(final MenuGroup menuGroup, final List<MenuProduct> menuProducts) {
        return 생성(null, menuGroup, this.price, menuProducts);
    }

    public Menu 생성(final MenuGroup menuGroup, final BigDecimal price) {
        return 생성(null, menuGroup, price, null);
    }

    public Menu 생성(final long id, final List<MenuProduct> menuProducts) {
        return 생성(id, null, this.price, menuProducts);
    }

    public Menu 생성(final Long id, final MenuGroup menuGroup, final BigDecimal price,
                   final List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setId(id);
        menu.setName(this.name);
        menu.setPrice(price);
        if (menuGroup != null) {
            menu.setMenuGroupId(menuGroup.getId());
        }
        menu.setMenuProducts(menuProducts);

        return menu;
    }
}
