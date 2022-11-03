package kitchenpos.product.domain;

import java.math.BigDecimal;

public class Product {

    private Long id;
    private String name;
    private BigDecimal price;

    public Product() {
    }

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

    public boolean isSameProductId(final Long productId) {
        return id.equals(productId);
    }

    public BigDecimal calculateAmount(final Quantity quantity) {
        if (quantity.isSameProductId(id)) {
            return price.multiply(BigDecimal.valueOf(quantity.getQuantity()));
        }
        return BigDecimal.ZERO;
    }
}