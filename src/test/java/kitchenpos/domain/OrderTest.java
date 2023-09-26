package kitchenpos.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class OrderTest {

    @Test
    void 저장을_위한_주문을_생성한다_이때_id_는_null_주문_상태는_조리_주문_시간은_현재_시간으로_초기화_한다() {
        // when
        OrderLineItem orderLineItem = OrderLineItem.createToSave(1L, 1L);
        List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItem);
        Order orderToSave = Order.createToSave(1L, orderLineItems);

        // then
        Long actualId = orderToSave.getId();
        String actualOrderStatus = orderToSave.getOrderStatus();
        String expectationOrderStatus = OrderStatus.COOKING.name();
        LocalDateTime actualOrderedTime = orderToSave.getOrderedTime();
        assertAll(
                () -> Assertions.assertThat(actualId).isNull(),
                () -> Assertions.assertThat(actualOrderStatus).isEqualTo(expectationOrderStatus),
                () -> Assertions.assertThat(actualOrderedTime).isNotNull()
        );
    }

    @Test
    void 저장을_위한_주문_생성시_주문_항목이_비어있으면_예외가_발생한다() {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        Assertions.assertThatThrownBy(
                () -> Order.createToSave(1L, orderLineItems)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문에_포함된_메뉴_id_를_모두_검색한다() {
        // given
        OrderLineItem orderLineItem1 = OrderLineItem.createToSave(1L, 1L);
        OrderLineItem orderLineItem2 = OrderLineItem.createToSave(2L, 1L);
        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);
        Order order = Order.createToSave(1L, orderLineItems);

        // when
        List<Long> menuIds = order.collectMenuIds();

        // then
        Assertions.assertThat(menuIds).containsOnly(1L, 2L);
    }

    @Test
    void 저장을_위한_유효성_검증시_주문의_주문_항목_개수와_실제_메뉴_개수가_같으면_예외가_발생하지_않는다() {
        // given
        OrderLineItem orderLineItem = OrderLineItem.createToSave(1L, 1L);
        List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItem);
        Order order = Order.createToSave(1L, orderLineItems);

        // when, then
        int actualMenuCount = 1;
        Assertions.assertThatCode(
                () -> order.validateToSave(actualMenuCount)
        ).doesNotThrowAnyException();
    }

    @Test
    void 저장을_위한_유효성_검증시_주문의_주문_항목_개수와_실제_메뉴_개수가_다른_경우_예외가_발생한다() {
        // given
        OrderLineItem orderLineItem = OrderLineItem.createToSave(1L, 1L);
        List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItem);
        Order order = Order.createToSave(1L, orderLineItems);

        // when, then
        int actualMenuCount = 2;
        Assertions.assertThatThrownBy(
                () -> order.validateToSave(actualMenuCount)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"COOKING", "MEAL"})
    void 주문의_주문_상태_변경시_주문_상태가_완료가_아니라면_예외가_발생하지_않는다(final OrderStatus validOrderStatus) {
        Order order = new Order(null, null, validOrderStatus.name(), null, null);
        Assertions.assertThatCode(
                () -> order.updateOrderStatus(OrderStatus.COOKING.name())
        ).doesNotThrowAnyException();
    }

    @Test
    void 주문의_주문_상태_변경시_주문_상태가_완료인_경우_예외가_발생한다() {
        Order order = new Order(null, null, OrderStatus.COMPLETION.name(), null, null);
        Assertions.assertThatThrownBy(
                () -> order.updateOrderStatus(OrderStatus.COOKING.name())
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class)
    void 주문의_주문_상태를_변경한다(final OrderStatus orderStatus) {
        // when
        Order order = new Order(null, null, OrderStatus.COOKING.name(), null, null);
        order.updateOrderStatus(orderStatus.name());

        // then
        String actualOrderStatus = order.getOrderStatus();
        String expectationOrderStatus = orderStatus.name();
        Assertions.assertThat(actualOrderStatus).isEqualTo(expectationOrderStatus);
    }

    @Test
    void 주문의_주문_상태를_변경할때_유효하지_않은_주문_상태인_경우_예외가_발생한다() {
        Order order = new Order();
        Assertions.assertThatThrownBy(
                () -> order.updateOrderStatus("invalidOrderStatus")
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
