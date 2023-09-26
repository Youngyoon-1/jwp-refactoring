package kitchenpos.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

public class MenuRequest {

    private String name;
    private BigDecimal price;
    private long menuGroupId;
    private List<MenuProductRequest> menuProducts;

    private MenuRequest() {
    }

    public MenuRequest(final String name, final BigDecimal price, final long menuGroupId,
                       final List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts.stream()
                .map(MenuProductRequest::new)
                .collect(Collectors.toList());
        this.name = name;
        this.price = price;
        this.menuGroupId = menuGroupId;
    }

    public Menu toEntity() {
        final List<MenuProduct> menuProducts = this.menuProducts.stream()
                .map(MenuProductRequest::toEntity)
                .collect(Collectors.toList());
        return new Menu(this.name, this.price, this.menuGroupId, menuProducts);
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public long getMenuGroupId() {
        return this.menuGroupId;
    }

    public List<MenuProductRequest> getMenuProducts() {
        return menuProducts;
    }
}
