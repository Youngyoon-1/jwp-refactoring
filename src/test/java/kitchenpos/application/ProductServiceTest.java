package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.dto.request.ProductRequest;
import kitchenpos.dto.response.ProductResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void 제품을_저장한다() {
        // given
        Product savedProduct = new Product(1L, "제품이름", BigDecimal.valueOf(1000));
        given(productRepository.save(ArgumentMatchers.any(Product.class)))
                .willReturn(savedProduct);

        // when
        ProductRequest productRequest = new ProductRequest("제품이름", BigDecimal.valueOf(1000));
        ProductResponse productResponse = productService.create(productRequest);

        // then
        long productId = productResponse.getId();
        String name = productResponse.getName();
        BigDecimal price = productResponse.getPrice();
        assertAll(
                () -> Assertions.assertThat(productId).isEqualTo(1L),
                () -> Assertions.assertThat(name).isEqualTo("제품이름"),
                () -> Assertions.assertThat(price).isEqualTo(BigDecimal.valueOf(1000)),
                () -> verify(productRepository).save(ArgumentMatchers.any(Product.class))
        );
    }

    @ParameterizedTest
    @CsvSource({"-1"})
    @NullSource
    void 제품을_저장할_때_제품의_가격이_null_또는_음수인_경우_예외가_발생한다(final BigDecimal invalidPrice) {
        // given
        ProductRequest productRequest = new ProductRequest("제품1", invalidPrice);

        // when, then
        assertThatThrownBy(() -> productService.create(productRequest))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("제품의 가격은 null 또는 음수일 수 없습니다.");
    }

    @Test
    void 제품_전체를_조회한다() {
        // given
        Product product = new Product(1L, "제품이름", BigDecimal.valueOf(1000));
        List<Product> savedProduct = Collections.singletonList(product);
        given(productRepository.findAll())
                .willReturn(savedProduct);

        // when
        List<ProductResponse> response = productService.list();

        // then
        ProductResponse productResponse = response.get(0);
        long productId = productResponse.getId();
        String name = productResponse.getName();
        BigDecimal price = productResponse.getPrice();
        assertAll(
                () -> Assertions.assertThat(productId).isEqualTo(1L),
                () -> Assertions.assertThat(name).isEqualTo("제품이름"),
                () -> Assertions.assertThat(price).isEqualTo(BigDecimal.valueOf(1000)),
                () -> verify(productRepository).findAll()
        );
    }
}
