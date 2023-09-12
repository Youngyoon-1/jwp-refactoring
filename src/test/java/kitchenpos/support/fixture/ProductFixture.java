package kitchenpos.support.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Product;

public enum ProductFixture {

    PRODUCT_1("제품1", BigDecimal.valueOf(1000)),
    PRODUCT_2("제품2", BigDecimal.valueOf(1000)),
    ;

    private final String name;
    private final BigDecimal price;

    ProductFixture(final String name, final BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public Product 생성() {
        final Product product = new Product();
        product.setName(this.name);
        product.setPrice(this.price);

        return product;
    }
}
