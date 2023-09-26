package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Product;
import kitchenpos.dto.request.ProductRequest;
import kitchenpos.dto.response.ProductResponse;
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
        ProductRequest productRequest = new ProductRequest("제품1", BigDecimal.ZERO);

        // when
        ResponseEntity<ProductResponse> response = testRestTemplate.postForEntity(
                "/api/products",
                productRequest,
                ProductResponse.class
        );

        // then
        String actualName = productRequest.getName();
        BigDecimal actualPrice = productRequest.getPrice();
        ProductResponse expectation = response.getBody();
        String expectationName = expectation.getName();
        BigDecimal expectationPrice = expectation.getPrice();
        assertAll(
                () -> assertThat(actualName).isEqualTo(expectationName),
                () -> assertThat(actualPrice).isCloseTo(expectationPrice, Percentage.withPercentage(0.1))
        );
    }

    @Test
    void 제품_전체를_조회한다() {
        // given
        ProductRequest productToSave = new ProductRequest("제품1", BigDecimal.ZERO);
        ResponseEntity<Product> savedProduct = testRestTemplate.postForEntity(
                "/api/products",
                productToSave,
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
