package kitchenpos.dto.request;

import java.math.BigDecimal;
import kitchenpos.domain.Product;

public class ProductRequest {

    private String name;
    private BigDecimal price;

    private ProductRequest() {
    }

    public ProductRequest(final String name, final BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public Product toEntity() {
        return new Product(this.name, this.price);
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }
}
