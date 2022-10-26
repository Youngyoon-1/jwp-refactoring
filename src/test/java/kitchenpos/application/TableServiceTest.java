package kitchenpos.application;

import static kitchenpos.application.TestFixture.메뉴_그룹_생성;
import static kitchenpos.application.TestFixture.메뉴_상품_생성;
import static kitchenpos.application.TestFixture.메뉴_생성;
import static kitchenpos.application.TestFixture.상품_생성;
import static kitchenpos.application.TestFixture.주문_상품_생성;
import static kitchenpos.application.TestFixture.주문_생성;
import static kitchenpos.application.TestFixture.주문_테이블_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class TableServiceTest extends ServiceTest {

    @Autowired
    private TableService tableService;

    @Test
    void 주문_테이블을_생성한다() {
        // given, when
        final OrderTable actual = tableService.create(주문_테이블_생성(1, true));

        // then
        assertAll(
                () -> assertThat(actual.getNumberOfGuests()).isOne(),
                () -> assertThat(actual.isEmpty()).isTrue()
        );
    }

    @Test
    void 주문_테이블_전체를_조회한다() {
        // given
        final OrderTable expected = orderTableDao.save(주문_테이블_생성(1, true));

        // when
        final List<OrderTable> actual = tableService.list();

        // then
        assertAll(
                () -> assertThat(actual.size()).isOne(),
                () -> assertThat(actual.get(0)).isEqualToComparingFieldByField(expected)
        );
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void 주문_테이블을_상태를_변경한다(final boolean expected) {
        // given
        final Long orderTableId = orderTableDao.save(주문_테이블_생성(1, true)).getId();

        // when
        final OrderTable actual = tableService.changeEmpty(orderTableId, 주문_테이블_생성(1, expected));

        // then
        assertThat(actual.isEmpty()).isEqualTo(expected);
    }

    @Test
    void 주문_테이블_상태_변경시_주문_테이블에_등록되지_않은_경우_예외가_발생한다() {
        // given, when, then
        assertThatThrownBy(() -> tableService.changeEmpty(1L, 주문_테이블_생성(1, false)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블_상태_변경시_주문_상태가_COOKING_인_경우_예외가_발생한다() {
        // given
        final OrderTable orderTable = orderTableDao.save(주문_테이블_생성(1, true));
        final Product product = productDao.save(상품_생성("테스트-상품", BigDecimal.valueOf(99999)));
        final MenuProduct menuProduct = 메뉴_상품_생성(product.getId(), 1L);
        final MenuGroup menuGroup = menuGroupDao.save(메뉴_그룹_생성("테스트-메뉴-그룹"));
        final Menu menu = menuDao.save(
                메뉴_생성("테스트-메뉴-1", BigDecimal.valueOf(99999), menuGroup.getId(), List.of(menuProduct)));
        final OrderLineItem orderLineItem = 주문_상품_생성(menu.getId());
        orderDao.save(주문_생성(List.of(orderLineItem), orderTable.getId(), OrderStatus.COOKING));

        // when, then
        assertThatThrownBy(() -> tableService.changeEmpty(orderTable.getId(), 주문_테이블_생성(1, false)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블_상태_변경시_주문_상태가_MEAL_인_경우_예외가_발생한다() {
        // given
        final OrderTable orderTable = orderTableDao.save(주문_테이블_생성(1, true));
        final Product product = productDao.save(상품_생성("테스트-상품", BigDecimal.valueOf(99999)));
        final MenuProduct menuProduct = 메뉴_상품_생성(product.getId(), 1L);
        final MenuGroup menuGroup = menuGroupDao.save(메뉴_그룹_생성("테스트-메뉴-그룹"));
        final Menu menu = menuDao.save(
                메뉴_생성("테스트-메뉴-1", BigDecimal.valueOf(99999), menuGroup.getId(), List.of(menuProduct)));
        final OrderLineItem orderLineItem = 주문_상품_생성(menu.getId());
        orderDao.save(주문_생성(List.of(orderLineItem), orderTable.getId(), OrderStatus.MEAL));

        // when, then
        assertThatThrownBy(() -> tableService.changeEmpty(orderTable.getId(), 주문_테이블_생성(1, false)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 테이블_손님_수를_변경한다() {
        // given
        final Long orderTableId = orderTableDao.save(주문_테이블_생성(1, false)).getId();

        // when
        final OrderTable actual = tableService.changeNumberOfGuests(orderTableId, 주문_테이블_생성(2, false));

        // then
        assertThat(actual.getNumberOfGuests()).isEqualTo(2);
    }

    @Test
    void 테이블_손님_수_변경시_테이블이_빈_상태면_예외가_발생한다() {
        // given
        final OrderTable orderTable = 주문_테이블_생성(1, false);

        // when, then
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 테이블_손님_수_변경시_변경하려는_손님_수가_음수면_예외가_발생한다() {
        // given
        final OrderTable orderTable = 주문_테이블_생성(-1, true);

        // when, then
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 테이블_손님_수_변경시_주문_테이블에_등록되지_않은_경우_예외가_발생한다() {
        // given, when, then
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, 주문_테이블_생성(2, true)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
