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
        return 생성(null, this.price);
    }

    public Product 생성(final Long id) {
        return 생성(id, this.price);
    }

    public Product 생성(final BigDecimal price) {
        return 생성(null, price);
    }

    public Product 생성(final Long id, final BigDecimal price) {
        final Product product = new Product();
        product.setId(id);
        product.setName(this.name);
        product.setPrice(price);

        return product;
    }
}
