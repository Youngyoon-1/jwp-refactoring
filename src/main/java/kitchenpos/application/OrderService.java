package kitchenpos.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderLineItemRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderValidator;
import kitchenpos.dto.request.OrderRequestToChangeOrderStatus;
import kitchenpos.dto.request.OrderRequestToCreate;
import kitchenpos.dto.response.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final OrderValidator orderValidator;

    public OrderService(
            final OrderRepository orderRepository,
            final OrderLineItemRepository orderLineItemRepository,
            final OrderValidator orderValidator
    ) {
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.orderValidator = orderValidator;
    }

    @Transactional
    public OrderResponse create(final OrderRequestToCreate orderRequest) {
        final Order order = orderRequest.toEntity();
        orderValidator.validateToCreateOrder(order);
        order.updateToSave();
        final Order savedOrder = orderRepository.save(order);
        final long orderId = savedOrder.getId();
        final List<OrderLineItem> savedOrderLineItems = new ArrayList<>();
        for (final OrderLineItem orderLineItem : savedOrder.getOrderLineItems()) {
            orderLineItem.updateOrderId(orderId);
            final OrderLineItem savedOrderLineItem = orderLineItemRepository.save(orderLineItem);
            savedOrderLineItems.add(savedOrderLineItem);
        }
        savedOrder.setOrderLineItems(savedOrderLineItems);
        return new OrderResponse(savedOrder);
    }

    public List<OrderResponse> list() {
        final List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse changeOrderStatus(final Long orderId, final OrderRequestToChangeOrderStatus orderRequest) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new IllegalArgumentException("저장되지 않은 주문입니다.")
                );
        orderValidator.validateToChangeOrderStatus(order);
        order.updateOrderStatus(orderRequest.getOrderStatus());
        return new OrderResponse(order);
    }
}
