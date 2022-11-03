package kitchenpos.menu.domain;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.product.domain.Quantity;

public class MenuProducts {

    private final List<MenuProduct> menuProducts;

    public MenuProducts(final List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
    }

    public List<Quantity> toQuantities() {
        return menuProducts.stream()
                .map(it -> new Quantity(it.getProductId(), it.getQuantity()))
                .collect(Collectors.toList());
    }

    public List<MenuProduct> getMenuProducts() {
        return menuProducts;
    }

    public List<Long> getProductIds() {
        return menuProducts.stream()
                .map(MenuProduct::getProductId)
                .collect(Collectors.toList());
    }
}