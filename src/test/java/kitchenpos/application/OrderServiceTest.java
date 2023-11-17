package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderLineItemRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderValidator;
import kitchenpos.dto.request.OrderLineItemRequestToCreate;
import kitchenpos.dto.request.OrderRequestToChangeOrderStatus;
import kitchenpos.dto.request.OrderRequestToCreate;
import kitchenpos.dto.response.OrderLineItemResponse;
import kitchenpos.dto.response.OrderResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLineItemRepository orderLineItemRepository;

    @Mock
    private OrderValidator orderValidator;

    @InjectMocks
    private OrderService orderService;

    @Test
    void 주문을_생성한다() {
        // given
        BDDMockito.willDoNothing()
                .given(orderValidator)
                .validateToCreateOrder(ArgumentMatchers.any(Order.class));
        OrderLineItem orderLineItem = new OrderLineItem(null, null, 1L, 1L);
        Order savedOrder = new Order(1L, 1L, OrderStatus.COOKING.name(), LocalDateTime.now(),
                Collections.singletonList(orderLineItem));
        given(orderRepository.save(ArgumentMatchers.any(Order.class)))
                .willReturn(savedOrder);
        OrderLineItem savedOrderLineItem = new OrderLineItem(1L, 1L, 1L, 1L);
        given(orderLineItemRepository.save(ArgumentMatchers.any(OrderLineItem.class)))
                .willReturn(savedOrderLineItem);

        // when
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(1L, 1L);
        OrderRequestToCreate orderRequestToCreate = new OrderRequestToCreate(1L,
                Collections.singletonList(orderLineItemRequest));
        OrderResponse orderResponse = orderService.create(orderRequestToCreate);

        // then
        Long orderId = orderResponse.getId();
        long orderTableId = orderResponse.getOrderTableId();
        String orderStatus = orderResponse.getOrderStatus();
        LocalDateTime orderedTime = orderResponse.getOrderedTime();
        OrderLineItemResponse orderLineItemResponse = orderResponse.getOrderLineItems()
                .get(0);
        long orderLineItemId = orderLineItemResponse.getSeq();
        long orderIdOfOrderLineItem = orderLineItemResponse.getOrderId();
        long menuId = orderLineItemResponse.getMenuId();
        long quantity = orderLineItemResponse.getQuantity();
        assertAll(
                () -> assertThat(orderId).isEqualTo(1L),
                () -> assertThat(orderTableId).isEqualTo(1L),
                () -> assertThat(orderStatus).isEqualTo(OrderStatus.COOKING.name()),
                () -> assertThat(orderedTime).isNotNull(),
                () -> assertThat(orderLineItemId).isEqualTo(1L),
                () -> assertThat(orderIdOfOrderLineItem).isEqualTo(1L),
                () -> assertThat(menuId).isEqualTo(1L),
                () -> assertThat(quantity).isEqualTo(1L),
                () -> verify(orderValidator).validateToCreateOrder(ArgumentMatchers.any(Order.class)),
                () -> verify(orderRepository).save(ArgumentMatchers.any(Order.class)),
                () -> verify(orderLineItemRepository).save(ArgumentMatchers.any(OrderLineItem.class))
        );
    }

    @Test
    void 주문_전체를_조회한다() {
        // given
        OrderLineItem savedOrderLineItem = new OrderLineItem(1L, 1L, 1L, 1L);
        Order savedOrder = new Order(1L, 1L, OrderStatus.COOKING.name(), LocalDateTime.now(),
                Collections.singletonList(savedOrderLineItem));
        given(orderRepository.findAll())
                .willReturn(Collections.singletonList(savedOrder));

        // when
        List<OrderResponse> response = orderService.list();

        // then
        OrderResponse orderResponse = response.get(0);
        long orderId = orderResponse.getId();
        long orderTableId = orderResponse.getOrderTableId();
        String orderStatus = orderResponse.getOrderStatus();
        LocalDateTime orderedTime = orderResponse.getOrderedTime();
        OrderLineItemResponse orderLineItemResponse = orderResponse.getOrderLineItems()
                .get(0);
        long orderLineItemId = orderLineItemResponse.getSeq();
        long orderIdOfOrderLineItem = orderLineItemResponse.getOrderId();
        long menuId = orderLineItemResponse.getMenuId();
        long quantity = orderLineItemResponse.getQuantity();
        assertAll(
                () -> assertThat(orderId).isEqualTo(1L),
                () -> assertThat(orderTableId).isEqualTo(1L),
                () -> assertThat(orderStatus).isEqualTo(OrderStatus.COOKING.name()),
                () -> assertThat(orderedTime).isNotNull(),
                () -> assertThat(orderLineItemId).isEqualTo(1L),
                () -> assertThat(orderIdOfOrderLineItem).isEqualTo(1L),
                () -> assertThat(menuId).isEqualTo(1L),
                () -> assertThat(quantity).isEqualTo(1L),
                () -> verify(orderRepository).findAll()
        );
    }

    @Test
    void 주문_상태를_식사로_변경한다() {
        // given
        OrderLineItem savedOrderLineItem = new OrderLineItem(1L, 1L, 1L, 1L);
        Order savedOrder = new Order(1L, 1L, OrderStatus.COOKING.name(), LocalDateTime.now(),
                Collections.singletonList(savedOrderLineItem));
        given(orderRepository.findById(1L))
                .willReturn(Optional.of(savedOrder));
        BDDMockito.willDoNothing()
                .given(orderValidator)
                .validateToChangeOrderStatus(savedOrder);

        // when
        OrderRequestToChangeOrderStatus orderRequest = new OrderRequestToChangeOrderStatus(OrderStatus.MEAL.name());
        OrderResponse response = orderService.changeOrderStatus(1L, orderRequest);

        // then
        long orderId = response.getId();
        long orderTableId = response.getOrderTableId();
        String orderStatus = response.getOrderStatus();
        LocalDateTime orderedTime = response.getOrderedTime();
        OrderLineItemResponse orderLineItemResponse = response.getOrderLineItems()
                .get(0);
        long orderLineItemId = orderLineItemResponse.getSeq();
        long orderIdOfOrderLineItem = orderLineItemResponse.getOrderId();
        long menuId = orderLineItemResponse.getMenuId();
        long quantity = orderLineItemResponse.getQuantity();
        assertAll(
                () -> assertThat(orderId).isEqualTo(1L),
                () -> assertThat(orderTableId).isEqualTo(1L),
                () -> assertThat(orderStatus).isEqualTo(OrderStatus.MEAL.name()),
                () -> assertThat(orderedTime).isNotNull(),
                () -> assertThat(orderLineItemId).isEqualTo(1L),
                () -> assertThat(orderIdOfOrderLineItem).isEqualTo(1L),
                () -> assertThat(menuId).isEqualTo(1L),
                () -> assertThat(quantity).isEqualTo(1L),
                () -> verify(orderRepository).findById(orderId),
                () -> BDDMockito.verify(orderValidator).validateToChangeOrderStatus(savedOrder)
        );
    }

    @Test
    void 주문_상태를_변경할_때_저장되지_않은_주문일_경우_예외가_발생한다() {
        // given
        given(orderRepository.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        OrderRequestToChangeOrderStatus orderRequest = new OrderRequestToChangeOrderStatus(OrderStatus.MEAL.name());
        assertAll(
                () -> assertThatThrownBy(() -> orderService.changeOrderStatus(1L, orderRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("저장되지 않은 주문입니다."),
                () -> verify(orderRepository).findById(1L)
        );
    }
}
