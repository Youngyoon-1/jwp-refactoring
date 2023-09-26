package kitchenpos.domain;

import java.math.BigDecimal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

public class MenuTest {


    @Test
    void 메뉴를_생성한다() {
        Assertions.assertThatCode(
                () -> new Menu("메뉴", BigDecimal.ZERO, 1L, null)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullSource
    @CsvSource("-1")
    void 메뉴의_가격이_null_또는_음수일_경우_예외가_발생한다(final BigDecimal invalidPrice) {
        Assertions.assertThatThrownBy(
                () -> new Menu("메뉴", invalidPrice, 1L, null)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_가격을_제품_전체_금액과_비교해서_유효성을_검증한다() {
        BigDecimal totalProductPrice = BigDecimal.ZERO;
        Menu menu = new Menu("메뉴", BigDecimal.ZERO, 1L, null);
        Assertions.assertThatCode(
                () -> menu.validatePriceWithProductPrice(totalProductPrice)
        ).doesNotThrowAnyException();
    }

    @Test
    void 메뉴_가격을_제품_전체_금액과_비교해서_메뉴_가격이_더_비싼_경우_예외가_발생한다() {
        BigDecimal totalProductPrice = BigDecimal.ONE;
        Menu menu = new Menu("메뉴", BigDecimal.TEN, 1L, null);
        Assertions.assertThatThrownBy(
                        () -> menu.validatePriceWithProductPrice(totalProductPrice)
                )
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
