package kitchenpos.dao;

import static kitchenpos.support.fixture.ProductFixture.PRODUCT_1;
import static kitchenpos.support.fixture.ProductFixture.PRODUCT_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

@JdbcTest
class JdbcTemplateProductDaoTest {

    private final JdbcTemplateProductDao jdbcTemplateProductDao;

    @Autowired
    private JdbcTemplateProductDaoTest(final DataSource datasource) {
        this.jdbcTemplateProductDao = new JdbcTemplateProductDao(datasource);
    }

    @Test
    void 제품을_저장한다() {
        // given
        Product product = PRODUCT_1.생성();

        // when
        Product savedProduct = jdbcTemplateProductDao.save(product);

        // then
        BigDecimal productPrice = product.getPrice();
        BigDecimal savedProductPrice = savedProduct.getPrice();
        assertAll(
                () -> assertThat(product).isEqualToIgnoringGivenFields(savedProduct, "id", "price"),
                () -> assertThat(productPrice).isEqualByComparingTo(savedProductPrice)
        );
    }

    @Test
    void ID_로_제품을_조회한다() {
        // given
        Product savedProduct = 제품_저장(PRODUCT_1.생성());
        long id = savedProduct.getId();

        // when
        Product selectedProduct = jdbcTemplateProductDao.findById(id)
                .get();

        // then
        assertThat(savedProduct).isEqualToComparingFieldByField(selectedProduct);
    }

    @Test
    void 제품_전체를_조회한다() {
        // given
        Product savedProduct1 = 제품_저장(PRODUCT_1.생성());
        Product savedProduct2 = 제품_저장(PRODUCT_2.생성());
        List<Product> savedProducts = new ArrayList<>();
        savedProducts.add(savedProduct1);
        savedProducts.add(savedProduct2);

        // when
        List<Product> selectedProducts = jdbcTemplateProductDao.findAll();

        // then
        assertThat(savedProducts).usingRecursiveComparison()
                .isEqualTo(selectedProducts);
    }

    private Product 제품_저장(Product product) {
        return jdbcTemplateProductDao.save(product);
    }
}