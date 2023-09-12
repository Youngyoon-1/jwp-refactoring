package kitchenpos.support.fixture;

import kitchenpos.domain.MenuGroup;

public enum MenuGroupFixture {

    MENU_GROUP_1("메뉴그룹1"),
    MENU_GROUP_2("메뉴그룹2"),
    ;

    private final String name;

    MenuGroupFixture(final String name) {
        this.name = name;
    }

    public MenuGroup 생성() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(this.name);

        return menuGroup;
    }
}
