package kitchenpos.dto.request;

import kitchenpos.domain.MenuProduct;

public class MenuProductRequest {

    private long productId;
    private long quantity;

    private MenuProductRequest() {
    }

    public MenuProductRequest(final MenuProduct menuProduct) {
        this.productId = menuProduct.getProductId();
        this.quantity = menuProduct.getQuantity();
    }

    public long getProductId() {
        return this.productId;
    }

    public long getQuantity() {
        return this.quantity;
    }

    public MenuProduct toEntity() {
        return new MenuProduct(this.productId, this.quantity);
    }
}
