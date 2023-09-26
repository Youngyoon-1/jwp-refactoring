package kitchenpos.application;

import static kitchenpos.support.fixture.OrderFixture.ORDER;
import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.request.OrderLineItemRequestToCreate;
import kitchenpos.dto.request.OrderRequestToChangeOrderStatus;
import kitchenpos.dto.request.OrderRequestToCreate;
import kitchenpos.dto.response.OrderLineItemResponse;
import kitchenpos.dto.response.OrderResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private MenuDao menuDao;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private OrderService orderService;

    @Test
    void 주문을_생성한다() {
        // given
        given(menuDao.countByIdIn(anyList()))
                .willReturn(1L);
        OrderTable orderTable = new OrderTable(1L, null, 1, false);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.of(orderTable));
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(1L, 1L);
        List<OrderLineItemRequestToCreate> orderLineItemRequests = Collections.singletonList(orderLineItemRequest);
        OrderRequestToCreate orderRequestToCreate = new OrderRequestToCreate(1L, orderLineItemRequests);
        Order order = orderRequestToCreate.toEntity();
        Order savedOrder = new Order(1L, order.getOrderTableId(), order.getOrderStatus(), order.getOrderedTime(), null);
        given(orderDao.save(ArgumentMatchers.any(Order.class)))
                .willReturn(savedOrder);
        OrderLineItem savedOrderLineItem = new OrderLineItem(1L, savedOrder.getId(), orderLineItemRequest.getMenuId(),
                orderLineItemRequest.getQuantity());
        given(orderLineItemDao.save(ArgumentMatchers.any(OrderLineItem.class)))
                .willReturn(savedOrderLineItem);

        // when
        OrderResponse orderResponse = orderService.create(orderRequestToCreate);

        // then
        Long orderLineItemId = orderResponse.getOrderLineItems()
                .get(0)
                .getId();
        assertAll(
                () -> assertThat(orderLineItemId).isEqualTo(1L),
                () -> verify(menuDao).countByIdIn(anyList()),
                () -> verify(orderTableDao).findById(1L),
                () -> verify(orderDao).save(ArgumentMatchers.any(Order.class)),
                () -> verify(orderLineItemDao).save(ArgumentMatchers.any(OrderLineItem.class))
        );
    }

    @Test
    void 주문을_생성할_때_주문_항목이_없으면_예외가_발생한다() {
        OrderRequestToCreate orderRequest = new OrderRequestToCreate(1L, new ArrayList<>());
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문을_생성할_때_등록되지_않은_주문_항목의_메뉴가_한_개라도_존재하면_예외가_발생한다() {
        // given
        given(menuDao.countByIdIn(anyList()))
                .willReturn(0L);

        // when, then
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(1L, 1L);
        List<OrderLineItemRequestToCreate> orderLineItemRequests = Collections.singletonList(orderLineItemRequest);
        OrderRequestToCreate orderRequestToCreate = new OrderRequestToCreate(1L, orderLineItemRequests);
        assertAll(
                () -> assertThatThrownBy(() -> orderService.create(orderRequestToCreate))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(menuDao).countByIdIn(anyList())
        );
    }

    @Test
    void 주문을_생성할_때_저장되지_않은_주문_테이블인_경우_예외가_발생한다() {
        // given
        given(menuDao.countByIdIn(anyList()))
                .willReturn(1L);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(1L, 1L);
        List<OrderLineItemRequestToCreate> orderLineItemRequests = Collections.singletonList(orderLineItemRequest);
        OrderRequestToCreate orderRequest = new OrderRequestToCreate(1L, orderLineItemRequests);
        assertAll(
                () -> assertThatThrownBy(() -> orderService.create(orderRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(menuDao).countByIdIn(anyList()),
                () -> verify(orderTableDao).findById(1L)
        );
    }

    @Test
    void 주문을_생성할_때_주문_테이블이_비어있는_경우_예외가_발생한다() {
        // given
        OrderTable orderTable = ORDER_TABLE.생성(true);
        given(menuDao.countByIdIn(anyList()))
                .willReturn(1L);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.of(orderTable));

        // when, then
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(1L, 1L);
        List<OrderLineItemRequestToCreate> orderLineItemRequests = Collections.singletonList(orderLineItemRequest);
        OrderRequestToCreate orderRequest = new OrderRequestToCreate(1L, orderLineItemRequests);
        assertAll(
                () -> assertThatThrownBy(() -> orderService.create(orderRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(menuDao).countByIdIn(anyList()),
                () -> verify(orderTableDao).findById(1L)
        );
    }

    @Test
    void 주문_전체를_조회한다() {
        // given
        Long orderId = 1L;
        Order savedOrder = new Order(orderId, 1L, OrderStatus.COOKING.name(), LocalDateTime.now(), null);
        List<Order> savedOrders = Collections.singletonList(savedOrder);
        OrderLineItem savedOrderLineItem = new OrderLineItem(1L, orderId, 1L, 1L);
        List<OrderLineItem> savedOrderLineItems = Collections.singletonList(savedOrderLineItem);
        given(orderDao.findAll())
                .willReturn(savedOrders);
        given(orderLineItemDao.findAllByOrderId(orderId))
                .willReturn(savedOrderLineItems);

        // when
        List<OrderResponse> selectedOrders = orderService.list();

        // then
        OrderLineItemResponse actualOrderLineItem = selectedOrders.get(0)
                .getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(actualOrderLineItem).isEqualToComparingFieldByField(savedOrderLineItem),
                () -> verify(orderDao).findAll(),
                () -> verify(orderLineItemDao).findAllByOrderId(orderId)
        );
    }

    @Test
    void 주문_상태를_식사로_변경한다() {
        // given
        long orderId = 1L;
        Order savedOrder = new Order(orderId, 1L, OrderStatus.COOKING.name(), LocalDateTime.now(), null);
        OrderLineItem savedOrderLineItem = new OrderLineItem(1L, orderId, 1L, 1L);
        List<OrderLineItem> savedOrderLineItems = Collections.singletonList(savedOrderLineItem);
        given(orderDao.findById(orderId))
                .willReturn(Optional.of(savedOrder));
        given(orderDao.save(savedOrder))
                .willReturn(null);
        given(orderLineItemDao.findAllByOrderId(orderId))
                .willReturn(savedOrderLineItems);

        // when
        OrderRequestToChangeOrderStatus orderRequest = new OrderRequestToChangeOrderStatus(OrderStatus.MEAL.name());
        OrderResponse updatedOrder = orderService.changeOrderStatus(orderId, orderRequest);

        // then
        String actualOrderStatus = updatedOrder.getOrderStatus();
        String expectationOrderStatus = orderRequest.getOrderStatus();
        OrderLineItemResponse actualOrderLineItem = updatedOrder.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(actualOrderStatus).isEqualTo(expectationOrderStatus),
                () -> assertThat(actualOrderLineItem).isEqualToComparingFieldByField(savedOrderLineItem),
                () -> verify(orderDao).findById(orderId),
                () -> verify(orderDao).save(savedOrder),
                () -> verify(orderLineItemDao).findAllByOrderId(orderId)
        );
    }

    @Test
    void 주문_상태를_변경할_때_저장되지_않은_주문일_경우_예외가_발생한다() {
        // given
        long orderIdRequest = 1L;
        given(orderDao.findById(orderIdRequest))
                .willReturn(Optional.empty());

        // when, then
        OrderRequestToChangeOrderStatus orderRequest = new OrderRequestToChangeOrderStatus(OrderStatus.MEAL.name());
        assertAll(
                () -> assertThatThrownBy(() -> orderService.changeOrderStatus(orderIdRequest, orderRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderDao).findById(orderIdRequest)
        );
    }

    @Test
    void 주문_상태를_변경할_때_변경_하려고_하는_주문의_주문_상태가_계산_완료인_경우_예외가_발생한다() {
        // given
        long orderIdRequest = 1L;
        Order order = ORDER.생성(OrderStatus.COMPLETION.toString());
        given(orderDao.findById(orderIdRequest))
                .willReturn(Optional.of(order));

        // when, then
        OrderRequestToChangeOrderStatus orderRequest = new OrderRequestToChangeOrderStatus(OrderStatus.MEAL.name());
        assertAll(
                () -> assertThatThrownBy(() -> orderService.changeOrderStatus(orderIdRequest, orderRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderDao).findById(orderIdRequest)
        );
    }
}
