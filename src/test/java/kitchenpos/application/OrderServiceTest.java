package kitchenpos.application;

import static kitchenpos.domain.OrderStatus.COOKING;
import static kitchenpos.domain.OrderStatus.MEAL;
import static kitchenpos.support.fixture.OrderFixture.ORDER;
import static kitchenpos.support.fixture.OrderLineItemFixture.ORDER_LINE_ITEM_1;
import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        OrderLineItem orderLineItemRequest = ORDER_LINE_ITEM_1.생성();
        List<OrderLineItem> orderLineItemsRequest = new ArrayList<>();
        orderLineItemsRequest.add(orderLineItemRequest);
        Order orderRequest = ORDER.생성(1L, 1L, orderLineItemsRequest);
        OrderTable orderTable = ORDER_TABLE.생성();
        Order order = ORDER.생성(1L);
        OrderLineItem orderLineItem = ORDER_LINE_ITEM_1.생성();
        given(menuDao.countByIdIn(anyList()))
                .willReturn(1L);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.of(orderTable));
        given(orderDao.save(orderRequest))
                .willReturn(order);
        given(orderLineItemDao.save(orderLineItemRequest))
                .willReturn(orderLineItem);

        // when
        Order createdOrder = orderService.create(orderRequest);

        // then
        Long orderId = orderRequest.getId();
        String orderStatus = orderRequest.getOrderStatus();
        LocalDateTime orderedTime = orderRequest.getOrderedTime();
        Long orderIdOfOrderLineItem = orderLineItemRequest.getOrderId();
        OrderLineItem createdOrderLineItem = createdOrder.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(orderId).isNull(),
                () -> assertThat(orderStatus).isEqualTo(COOKING.toString()),
                () -> assertThat(orderedTime).isNotNull(),
                () -> assertThat(orderIdOfOrderLineItem).isEqualTo(1L),
                () -> assertThat(orderLineItem).isEqualToComparingFieldByField(createdOrderLineItem),
                () -> verify(menuDao).countByIdIn(anyList()),
                () -> verify(orderTableDao).findById(1L),
                () -> verify(orderDao).save(orderRequest),
                () -> verify(orderLineItemDao).save(orderLineItemRequest)
        );
    }

    @Test
    void 주문을_생성할_때_주문_항목이_없으면_예외가_발생한다() {
        // given
        Order orderRequest = ORDER.생성();

        // when, then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문을_생성할_때_등록되지_않은_주문_항목의_메뉴가_한_개라도_존재하면_예외가_발생한다() {
        // given
        OrderLineItem orderLineItemRequest = ORDER_LINE_ITEM_1.생성();
        List<OrderLineItem> orderLineItemsRequest = new ArrayList<>();
        orderLineItemsRequest.add(orderLineItemRequest);
        Order orderRequest = ORDER.생성(orderLineItemsRequest);
        given(menuDao.countByIdIn(anyList()))
                .willReturn(0L);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> orderService.create(orderRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(menuDao).countByIdIn(anyList())
        );
    }

    @Test
    void 주문을_생성할_때_저장되지_않은_주문_테이블인_경우_예외가_발생한다() {
        // given
        OrderLineItem orderLineItemRequest = ORDER_LINE_ITEM_1.생성();
        List<OrderLineItem> orderLineItemsRequest = new ArrayList<>();
        orderLineItemsRequest.add(orderLineItemRequest);
        Order orderRequest = ORDER.생성(1L, orderLineItemsRequest);
        given(menuDao.countByIdIn(anyList()))
                .willReturn(1L);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.empty());

        // when, then
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
        OrderLineItem orderLineItemRequest = ORDER_LINE_ITEM_1.생성();
        List<OrderLineItem> orderLineItemsRequest = new ArrayList<>();
        orderLineItemsRequest.add(orderLineItemRequest);
        Order orderRequest = ORDER.생성(1L, orderLineItemsRequest);
        OrderTable orderTable = ORDER_TABLE.생성(true);
        given(menuDao.countByIdIn(anyList()))
                .willReturn(1L);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.of(orderTable));

        // when, then
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
        Order order = ORDER.생성(1L);
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        OrderLineItem orderLineItem = ORDER_LINE_ITEM_1.생성();
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);
        given(orderDao.findAll())
                .willReturn(orders);
        given(orderLineItemDao.findAllByOrderId(1L))
                .willReturn(orderLineItems);

        // when
        List<Order> selectedOrders = orderService.list();

        // then
        OrderLineItem selectedOrderLineItem = selectedOrders.get(0)
                .getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(orderLineItem).isEqualToComparingFieldByField(selectedOrderLineItem),
                () -> verify(orderDao).findAll(),
                () -> verify(orderLineItemDao).findAllByOrderId(1L)
        );
    }

    @Test
    void 주문_상태를_식사로_변경한다() {
        // given
        long orderIdRequest = 1L;
        Order orderRequest = ORDER.생성(MEAL.toString());
        Order order = ORDER.생성();
        OrderLineItem orderLineItem = ORDER_LINE_ITEM_1.생성();
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);
        given(orderDao.findById(orderIdRequest))
                .willReturn(Optional.of(order));
        given(orderDao.save(order))
                .willReturn(null);
        given(orderLineItemDao.findAllByOrderId(orderIdRequest))
                .willReturn(orderLineItems);

        // when
        Order updatedOrder = orderService.changeOrderStatus(orderIdRequest, orderRequest);

        // then
        String orderStatus = updatedOrder.getOrderStatus();
        OrderLineItem orderLineItemOfUpdatedOrder = updatedOrder.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(orderStatus).isEqualTo(MEAL.toString()),
                () -> assertThat(orderLineItem).isEqualToComparingFieldByField(orderLineItemOfUpdatedOrder),
                () -> verify(orderDao).findById(orderIdRequest),
                () -> verify(orderDao).save(order),
                () -> verify(orderLineItemDao).findAllByOrderId(orderIdRequest)
        );
    }

    @Test
    void 주문_상태를_변경할_때_저장되지_않은_주문일_경우_예외가_발생한다() {
        // given
        long orderIdRequest = 1L;
        Order orderRequest = ORDER.생성();
        given(orderDao.findById(orderIdRequest))
                .willReturn(Optional.empty());

        // when, then
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
        Order orderRequest = ORDER.생성();
        Order order = ORDER.생성(OrderStatus.COMPLETION.toString());
        given(orderDao.findById(orderIdRequest))
                .willReturn(Optional.of(order));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> orderService.changeOrderStatus(orderIdRequest, orderRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderDao).findById(orderIdRequest)
        );
    }
}
