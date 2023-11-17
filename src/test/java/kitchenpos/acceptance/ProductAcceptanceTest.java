package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.dto.request.ProductRequest;
import kitchenpos.dto.response.ProductResponse;
import org.assertj.core.api.Assertions;
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
        ProductRequest request = new ProductRequest("제품이름", BigDecimal.valueOf(1000));

        // when
        ResponseEntity<ProductResponse> response = testRestTemplate.postForEntity(
                "/api/products",
                request,
                ProductResponse.class
        );

        // then
        ProductResponse productResponse = response.getBody();
        long productId = productResponse.getId();
        String productName = productResponse.getName();
        BigDecimal price = productResponse.getPrice();
        assertAll(
                () -> assertThat(productId).isNotNull(),
                () -> assertThat(productName).isEqualTo("제품이름"),
                () -> assertThat(price).isEqualTo(BigDecimal.valueOf(1000))
        );
    }

    @Test
    void 제품_한_개를_저장하고_전체를_조회한다() {
        // given
        ProductRequest request = new ProductRequest("제품이름", BigDecimal.valueOf(1000));

        // when
        ResponseEntity<ProductResponse> responseToSave = testRestTemplate.postForEntity(
                "/api/products",
                request,
                ProductResponse.class
        );
        ResponseEntity<List<ProductResponse>> responseToSelect = testRestTemplate.exchange(
                "/api/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProductResponse>>() {
                }
        );

        // then
        ProductResponse productResponseToSave = responseToSave.getBody();
        ProductResponse productResponseToSelect = responseToSelect.getBody()
                .get(0);
        long priceToSave = productResponseToSave.getPrice()
                .longValue();
        long priceToSelect = productResponseToSelect.getPrice()
                .longValue();
        assertAll(
                () -> assertThat(productResponseToSave).isEqualToIgnoringGivenFields(productResponseToSelect, "price"),
                () -> Assertions.assertThat(priceToSave).isEqualTo(priceToSelect)
        );
    }
}
