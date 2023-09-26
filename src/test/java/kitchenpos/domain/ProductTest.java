package kitchenpos.domain;

import java.math.BigDecimal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

public class ProductTest {

    @Test
    void 상품을_생성한다() {
        Assertions.assertThatCode(
                () -> new Product("제품1", BigDecimal.ZERO)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullSource
    @CsvSource("-1")
    void 상품_가격이_NULL_또는_음수일_경우_예외가_발생한다(final BigDecimal invalidPrice) {
        Assertions.assertThatThrownBy(
                () -> new Product("제품1", invalidPrice)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
