package kitchenpos.dto.response;

import kitchenpos.domain.MenuProduct;

public class MenuProductResponse {

    private long seq;
    private long menuId;
    private long productId;
    private long quantity;

    private MenuProductResponse() {
    }

    public MenuProductResponse(final MenuProduct menuProduct) {
        this.seq = menuProduct.getSeq();
        this.menuId = menuProduct.getMenuId();
        this.productId = menuProduct.getProductId();
        this.quantity = menuProduct.getQuantity();
    }

    public long getSeq() {
        return this.seq;
    }

    public long getMenuId() {
        return this.menuId;
    }

    public long getProductId() {
        return this.productId;
    }

    public long getQuantity() {
        return this.quantity;
    }
}
