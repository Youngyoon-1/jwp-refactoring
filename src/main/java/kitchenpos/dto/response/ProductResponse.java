package kitchenpos.dto.response;

import java.math.BigDecimal;
import kitchenpos.domain.Product;

public class ProductResponse {

    private long id;
    private String name;
    private BigDecimal price;

    private ProductResponse() {
    }

    public ProductResponse(final Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }
}
