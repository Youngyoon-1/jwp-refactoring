package kitchenpos.acceptance;

import static kitchenpos.support.fixture.ProductFixture.PRODUCT_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Product;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


@AcceptanceTest
public class ProductAcceptanceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void 제품을_한_개_등록한다() {
        // given
        Product request = PRODUCT_1.생성();

        // when
        ResponseEntity<Product> response = testRestTemplate.postForEntity(
                "/api/products",
                request,
                Product.class
        );

        // then
        Product actual = request;
        BigDecimal actualPrice = actual.getPrice();
        Product expectation = response.getBody();
        BigDecimal expectationPrice = expectation.getPrice();
        assertAll(
                () -> assertThat(request).isEqualToIgnoringGivenFields(expectation, "id", "price"),
                () -> assertThat(actualPrice).isCloseTo(expectationPrice, Percentage.withPercentage(0.1))
        );
    }

    @Test
    void 제품_전체를_조회한다() {
        // given
        Product product = PRODUCT_1.생성();
        ResponseEntity<Product> savedProduct = testRestTemplate.postForEntity(
                "/api/products",
                product,
                Product.class
        );

        // when
        ResponseEntity<List<Product>> response = testRestTemplate.exchange(
                "/api/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {
                }
        );

        // then
        Product actual = savedProduct.getBody();
        Product expectation = response.getBody()
                .get(0);
        assertThat(actual).isEqualToComparingFieldByField(expectation);
    }
}
