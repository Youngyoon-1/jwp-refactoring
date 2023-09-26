package kitchenpos.support.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public enum MenuProductFixture {

    MENU_PRODUCT(1),
    ;

    private final int quantity;

    MenuProductFixture(final int quantity) {
        this.quantity = quantity;
    }

    public MenuProduct 생성(final Product product) {
        return 생성(null, null, product, this.quantity);
    }

    public MenuProduct 생성(final Menu menu, final Product product) {
        return 생성(null, menu, product, this.quantity);
    }

    public MenuProduct 생성(final long id, final Product product) {
        return 생성(id, null, product, this.quantity);
    }

    public MenuProduct 생성(final Product product, final int quantity) {
        return 생성(null, null, product, quantity);
    }

    public MenuProduct 생성(final Menu menu, final Product product, final int quantity) {
        return 생성(null, menu, product, quantity);
    }

    public MenuProduct 생성(final Long id, final Menu menu, final Product product) {
        return 생성(id, menu, product, this.quantity);
    }

    public MenuProduct 생성(final Long id, final Menu menu, final Product product, final Integer quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(id);
        if (menu != null) {
            menuProduct.updateMenuId(menu.getId());
        }
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }
}
