package kitchenpos.application;

import static kitchenpos.application.TestFixture.메뉴_그룹_생성;
import static kitchenpos.application.TestFixture.메뉴_상품_생성;
import static kitchenpos.application.TestFixture.메뉴_생성;
import static kitchenpos.application.TestFixture.상품_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MenuServiceTest extends ServiceTest {

    @Autowired
    private MenuService menuService;

    @Test
    void 메뉴를_생성한다() {
        // given
        final MenuGroup menuGroup = 메뉴_그룹을_저장한다(메뉴_그룹_생성("테스트-메뉴-그룹"));
        final Product product = 상품을_저장한다(상품_생성("테스트-상품", BigDecimal.valueOf(99999)));
        final MenuProduct menuProduct = 메뉴_상품_생성(product.getId(), 1L);
        final Menu menu = 메뉴_생성("테스트-메뉴", product.getPrice(), menuGroup.getId(), List.of(menuProduct));

        // when
        final Menu actual = menuService.create(menu);

        // then
        assertThat(actual.getName()).isEqualTo("테스트-메뉴");
    }

    @Test
    void 메뉴_전체를_조회한다() {
        // given
        final MenuGroup menuGroup = 메뉴_그룹을_저장한다(메뉴_그룹_생성("테스트-메뉴-그룹"));
        final Product product = 상품을_저장한다(상품_생성("테스트-상품", BigDecimal.valueOf(99999)));
        final MenuProduct menuProduct = 메뉴_상품_생성(product.getId(), 1L);
        final Menu expected = 메뉴_생성("테스트-메뉴", product.getPrice(), menuGroup.getId(), List.of(menuProduct));
        메뉴를_저장한다(expected);

        // when
        final List<Menu> actual = menuService.list();

        // then
        assertThat(actual).usingElementComparatorOnFields("name")
                .containsExactly(expected);
    }

    @Test
    void 등록되지_않은_메뉴_그룹으로_메뉴_생성시_예외가_발생한다() {
        // given
        final MenuGroup menuGroup = 메뉴_그룹_생성("테스트-메뉴-그룹");
        final Product product = 상품을_저장한다(상품_생성("테스트-상품", BigDecimal.valueOf(99999)));
        final MenuProduct menuProduct = 메뉴_상품_생성(product.getId(), 1L);
        final Menu menu = 메뉴_생성("테스트-메뉴", product.getPrice(), menuGroup.getId(), List.of(menuProduct));

        // when,then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성시_메뉴_금액이_null_인_경우_예외가_발생한다() {
        // given
        final MenuGroup menuGroup = 메뉴_그룹을_저장한다(메뉴_그룹_생성("테스트-메뉴-그룹"));
        final Product product = 상품을_저장한다(상품_생성("테스트-상품", BigDecimal.valueOf(99999)));
        final MenuProduct menuProduct = 메뉴_상품_생성(product.getId(), 1L);
        final Menu menu = 메뉴_생성("테스트-메뉴", null, menuGroup.getId(), List.of(menuProduct));

        // when,then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성시_메뉴_금액이_음수_인_경우_예외가_발생한다() {
        // given
        final MenuGroup menuGroup = 메뉴_그룹을_저장한다(메뉴_그룹_생성("테스트-메뉴-그룹"));
        final Product product = 상품을_저장한다(상품_생성("테스트-상품", BigDecimal.valueOf(99999)));
        final MenuProduct menuProduct = 메뉴_상품_생성(product.getId(), 1L);
        final Menu menu = 메뉴_생성("테스트-메뉴", BigDecimal.valueOf(-1), menuGroup.getId(), List.of(menuProduct));

        // when,then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성시_메뉴_금액이_메뉴에_포함된_상품_전체_금액보다_클_경우_예외가_발생한다() {
        // given
        final MenuGroup menuGroup = 메뉴_그룹을_저장한다(메뉴_그룹_생성("테스트-메뉴-그룹"));
        final Product product = 상품을_저장한다(상품_생성("테스트-상품", BigDecimal.valueOf(99999)));
        final MenuProduct menuProduct = 메뉴_상품_생성(product.getId(), 1L);
        final Menu menu = 메뉴_생성("테스트-메뉴", BigDecimal.valueOf(100000), menuGroup.getId(), List.of(menuProduct));

        // when,then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
