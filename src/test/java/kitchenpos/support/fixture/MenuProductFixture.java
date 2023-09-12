package kitchenpos.support.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public enum MenuProductFixture {

    MENU_PRODUCT_1(1),
    MENU_PRODUCT_2(1),
    ;

    private final int quantity;

    MenuProductFixture(final int quantity) {
        this.quantity = quantity;
    }

    public MenuProduct 생성(final Menu menu, final Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(menu.getId());
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(this.quantity);

        return menuProduct;
    }
}
