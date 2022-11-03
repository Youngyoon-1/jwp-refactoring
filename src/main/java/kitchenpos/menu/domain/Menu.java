package kitchenpos.menu.domain;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.product.domain.Products;

public class Menu {

    private Long id;
    private String name;
    private BigDecimal price;
    private Long menuGroupId;
    private MenuProducts menuProducts;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public Long getMenuGroupId() {
        return menuGroupId;
    }

    public void setMenuGroupId(final Long menuGroupId) {
        this.menuGroupId = menuGroupId;
    }

    public List<MenuProduct> getMenuProducts() {
        return menuProducts.getMenuProducts();
    }

    public void setMenuProducts(final List<MenuProduct> menuProducts) {
        this.menuProducts = new MenuProducts(menuProducts);
    }

    public List<Long> getProductIds() {
        return menuProducts.getProductIds();
    }

    public void validatePrice(final Products products) {
        final BigDecimal amount = products.calculateAmount(menuProducts.toQuantities());
        if (isNullOrNegativePrice() || isGreaterPriceThan(amount)) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isNullOrNegativePrice() {
        return null == price || price.compareTo(BigDecimal.ZERO) < 0;
    }

    private boolean isGreaterPriceThan(final BigDecimal sum) {
        return price.compareTo(sum) > 0;
    }
}