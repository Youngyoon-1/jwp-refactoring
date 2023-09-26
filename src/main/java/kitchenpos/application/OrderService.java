package kitchenpos.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.request.OrderRequestToChangeOrderStatus;
import kitchenpos.dto.request.OrderRequestToCreate;
import kitchenpos.dto.response.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final MenuDao menuDao;
    private final OrderDao orderDao;
    private final OrderLineItemDao orderLineItemDao;
    private final OrderTableDao orderTableDao;

    public OrderService(
            final MenuDao menuDao,
            final OrderDao orderDao,
            final OrderLineItemDao orderLineItemDao,
            final OrderTableDao orderTableDao
    ) {
        this.menuDao = menuDao;
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.orderTableDao = orderTableDao;
    }

    @Transactional
    public OrderResponse create(final OrderRequestToCreate orderRequest) {
        final Order order = orderRequest.toEntity();
        final List<Long> menuIds = order.collectMenuIds();
        final long actualMenuCount = menuDao.countByIdIn(menuIds);
        order.validateToSave(actualMenuCount);

        final OrderTable orderTable = orderTableDao.findById(order.getOrderTableId())
                .orElseThrow(IllegalArgumentException::new);
        orderTable.validateToSaveOrder();

        final Order savedOrder = orderDao.save(order);
        final List<OrderLineItem> orderLineItems = order.getOrderLineItems();
        final List<OrderLineItem> savedOrderLineItems = new ArrayList<>();
        for (final OrderLineItem orderLineItem : orderLineItems) {
            orderLineItem.updateOrderIdByOrder(savedOrder);
            savedOrderLineItems.add(orderLineItemDao.save(orderLineItem));
        }
        savedOrder.setOrderLineItems(savedOrderLineItems);

        return new OrderResponse(savedOrder);
    }

    public List<OrderResponse> list() {
        final List<Order> orders = orderDao.findAll();

        for (final Order order : orders) {
            order.setOrderLineItems(orderLineItemDao.findAllByOrderId(order.getId()));
        }

        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse changeOrderStatus(final Long orderId, final OrderRequestToChangeOrderStatus orderRequest) {
        final Order savedOrder = orderDao.findById(orderId)
                .orElseThrow(IllegalArgumentException::new);
        savedOrder.updateOrderStatus(orderRequest.getOrderStatus());
        orderDao.save(savedOrder);
        savedOrder.setOrderLineItems(orderLineItemDao.findAllByOrderId(orderId));
        return new OrderResponse(savedOrder);
    }
}
