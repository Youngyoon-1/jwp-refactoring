package kitchenpos.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MenuValidatorTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productDao;

    @InjectMocks
    private MenuValidator menuValidator;

    @Test
    void 메뉴의_유효성_검증을_한다() {
        MenuProduct menuProduct1 = new MenuProduct(1L, 1L);
        MenuProduct menuProduct2 = new MenuProduct(2L, 1L);
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct1, menuProduct2);
        BDDMockito.given(menuGroupRepository.existsById(1L))
                .willReturn(true);
        Product product1 = new Product(1L, "제품1", BigDecimal.valueOf(500));
        Product product2 = new Product(2L, "제품2", BigDecimal.valueOf(500));
        BDDMockito.given(productDao.findById(1L))
                .willReturn(Optional.of(product1));
        BDDMockito.given(productDao.findById(2L))
                .willReturn(Optional.of(product2));
        Menu menu = new Menu("메뉴", BigDecimal.valueOf(1000), 1L, menuProducts);

        assertAll(
                () -> Assertions.assertThatCode(
                        () -> menuValidator.validate(menu)
                ).doesNotThrowAnyException(),
                () -> BDDMockito.verify(menuGroupRepository)
                        .existsById(1L),
                () -> BDDMockito.verify(productDao).findById(1L),
                () -> BDDMockito.verify(productDao).findById(2L)
        );
    }

    @Test
    void 메뉴_유효성_검증을_할_때_저장되지_않은_메뉴_그룹_id_일_경우_예외가_발생한다() {
        MenuProduct menuProduct = new MenuProduct(1L, 1L);
        Menu menu = new Menu("메뉴", BigDecimal.valueOf(1000), null, Collections.singletonList(menuProduct));
        Product product = new Product(1L, "제품", BigDecimal.valueOf(1000));
        BDDMockito.given(productDao.findById(1L))
                .willReturn(Optional.of(product));
        BDDMockito.given(menuGroupRepository.existsById(ArgumentMatchers.isNull()))
                .willReturn(false);

        assertAll(
                () -> Assertions.assertThatThrownBy(
                                () -> menuValidator.validate(menu)
                        ).isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("저장되지 않은 메뉴그룹 ID 입니다."),
                () -> BDDMockito.verify(productDao)
                        .findById(1L),
                () -> BDDMockito.verify(menuGroupRepository)
                        .existsById(ArgumentMatchers.isNull())
        );
    }

    @Test
    void 메뉴_유효성_검증시_저장되지_않은_제품이_존재하면_예외가_발생한다() {
        MenuProduct menuProduct = new MenuProduct();
        Menu menu = new Menu(null, BigDecimal.ONE, null, Collections.singletonList(menuProduct));
        BDDMockito.given(productDao.findById(ArgumentMatchers.isNull()))
                .willReturn(Optional.empty());

        assertAll(
                () -> Assertions.assertThatThrownBy(
                                () -> menuValidator.validate(menu)
                        ).isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("저장되지 않은 제품 ID 입니다."),
                () -> BDDMockito.verify(productDao).findById(ArgumentMatchers.isNull())
        );
    }

    @Test
    void 메뉴_유효성_검증시_메뉴가격이_제품가격의_총합보다_큰_경우_예외가_발생한다() {
        Product product1 = new Product("제품1", BigDecimal.valueOf(500));
        BDDMockito.given(productDao.findById(1L))
                .willReturn(Optional.of(product1));
        Product product2 = new Product("제품2", BigDecimal.valueOf(500));
        BDDMockito.given(productDao.findById(2L))
                .willReturn(Optional.of(product2));
        MenuProduct menuProduct1 = new MenuProduct(1L, 1L);
        MenuProduct menuProduct2 = new MenuProduct(2L, 1L);
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct1, menuProduct2);
        Menu menu = new Menu("메뉴", BigDecimal.valueOf(1001), null, menuProducts);

        assertAll(
                () -> Assertions.assertThatThrownBy(
                                () -> menuValidator.validate(menu)
                        ).isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("메뉴가격은 제품가격의 총합보다 클 수 없습니다."),
                () -> BDDMockito.verify(productDao).findById(1L),
                () -> BDDMockito.verify(productDao).findById(2L)
        );
    }

    @ParameterizedTest
    @NullSource
    @CsvSource("-1")
    void 메뉴_유효성_검증시_메뉴의_가격이_null_또는_음수일_경우_예외가_발생한다(final BigDecimal invalidPrice) {
        Menu menu = new Menu(null, invalidPrice, null, null);

        Assertions.assertThatThrownBy(
                        () -> menuValidator.validate(menu)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("메뉴가격은 null 또는 음수일 수 없습니다.");
    }
}
