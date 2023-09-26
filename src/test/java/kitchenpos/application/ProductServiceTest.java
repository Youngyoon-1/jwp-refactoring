package kitchenpos.application;

import static kitchenpos.support.fixture.ProductFixture.PRODUCT_1;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Product;
import kitchenpos.dto.request.ProductRequest;
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
    private ProductDao productDao;

    @InjectMocks
    private ProductService productService;

    @Test
    void 제품을_저장한다() {
        // given
        ProductRequest productRequest = new ProductRequest("제품1", BigDecimal.ZERO);
        Product willReturnValue = PRODUCT_1.생성(1L);
        given(productDao.save(ArgumentMatchers.any(Product.class)))
                .willReturn(willReturnValue);

        // when
        productService.create(productRequest);

        // then
        verify(productDao).save(ArgumentMatchers.any(Product.class));
    }

    @ParameterizedTest
    @CsvSource({"-1"})
    @NullSource
    void 제품을_저장할_때_제품의_가격이_null_또는_음수인_경우_예외가_발생한다(final BigDecimal invalidPrice) {
        // given
        ProductRequest productRequest = new ProductRequest("제품1", invalidPrice);

        // when, then
        assertThatThrownBy(() -> productService.create(productRequest))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 제품_전체를_조회한다() {
        // given
        Product product = PRODUCT_1.생성(1L);
        List<Product> willReturnValue = Collections.singletonList(product);
        given(productDao.findAll())
                .willReturn(willReturnValue);

        // when
        productService.list();

        // then
        verify(productDao).findAll();
    }
}
