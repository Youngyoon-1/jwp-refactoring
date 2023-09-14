package kitchenpos.application;

import static kitchenpos.support.fixture.ProductFixture.PRODUCT_1;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
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
        Product productRequest = PRODUCT_1.생성(BigDecimal.valueOf(0));
        given(productDao.save(productRequest))
                .willReturn(null);

        // when
        productService.create(productRequest);

        // then
        verify(productDao).save(productRequest);
    }

    @ParameterizedTest
    @CsvSource({"-1"})
    @NullSource
    void 제품을_저장할_때_제품의_가격이_null_또는_음수인_경우_예외가_발생한다(final BigDecimal invalidPrice) {
        // given
        Product productRequest = PRODUCT_1.생성(invalidPrice);

        // when, then
        assertThatThrownBy(() -> productService.create(productRequest))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 제품_전체를_조회한다() {
        // given
        given(productDao.findAll())
                .willReturn(null);

        // when
        productService.list();

        // then
        verify(productDao).findAll();
    }
}
