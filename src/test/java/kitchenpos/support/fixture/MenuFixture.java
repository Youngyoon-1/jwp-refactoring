package kitchenpos.support.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;

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

    public Menu 생성(final MenuGroup menuGroup) {
        final Menu menu = new Menu();
        menu.setName(this.name);
        menu.setPrice(this.price);
        menu.setMenuGroupId(menuGroup.getId());

        return menu;
    }
}
