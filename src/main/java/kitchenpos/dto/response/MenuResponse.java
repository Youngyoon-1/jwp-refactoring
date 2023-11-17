package kitchenpos.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

public class MenuResponse {

    private long id;
    private String name;
    private BigDecimal price;
    private long menuGroupId;
    private List<MenuProductResponse> menuProducts;

    private MenuResponse() {
    }

    public MenuResponse(final Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.menuGroupId = menu.getMenuGroupId();
        final List<MenuProduct> menuProducts = menu.getMenuProducts();
        this.menuProducts = menuProducts.stream()
                .map(MenuProductResponse::new)
                .collect(Collectors.toList());
    }

    public long getId() {
        return this.id;
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

    public List<MenuProductResponse> getMenuProducts() {
        return this.menuProducts;
    }
}
