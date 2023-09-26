package kitchenpos.acceptance;

import static kitchenpos.domain.OrderStatus.COOKING;
import static kitchenpos.support.fixture.MenuFixture.MENU_1;
import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import kitchenpos.dao.JdbcTemplateMenuDao;
import kitchenpos.dao.JdbcTemplateMenuGroupDao;
import kitchenpos.dao.JdbcTemplateOrderTableDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.request.OrderLineItemRequestToCreate;
import kitchenpos.dto.request.OrderRequestToChangeOrderStatus;
import kitchenpos.dto.request.OrderRequestToCreate;
import kitchenpos.dto.response.OrderLineItemResponse;
import kitchenpos.dto.response.OrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


@AcceptanceTest
public class OrderAcceptanceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplateOrderTableDao jdbcTemplateOrderTable;

    @Autowired
    private JdbcTemplateMenuGroupDao jdbcTemplateMenuGroupDao;

    @Autowired
    private JdbcTemplateMenuDao jdbcTemplateMenuDao;

    @Test
    void 주문_한_개를_등록한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성();
        MenuGroup savedMenuGroup = jdbcTemplateMenuGroupDao.save(menuGroup);
        Menu menu = MENU_1.생성(savedMenuGroup);
        Long menuId = jdbcTemplateMenuDao.save(menu)
                .getId();
        OrderTable orderTable = ORDER_TABLE.생성();
        Long orderTableId = jdbcTemplateOrderTable.save(orderTable)
                .getId();

        // when
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(menuId, 1L);
        List<OrderLineItemRequestToCreate> orderLineItemRequests = Collections.singletonList(orderLineItemRequest);
        OrderRequestToCreate request = new OrderRequestToCreate(orderTableId, orderLineItemRequests);
        ResponseEntity<OrderResponse> response = testRestTemplate.postForEntity(
                "/api/orders",
                request,
                OrderResponse.class
        );

        // then
        OrderResponse orderResponse = response.getBody();
        Long actualOrderId = orderResponse.getId();
        Long actualOrderTableId = orderResponse.getOrderTableId();
        Long expectationOrderTableId = orderTableId;
        String actualOrderStatus = orderResponse.getOrderStatus();
        String expectationOrderStatus = COOKING.name();
        LocalDateTime actualOrderedTime = orderResponse.getOrderedTime();
        OrderLineItemResponse actualOrderLineItemResponse = orderResponse.getOrderLineItems()
                .get(0);
        Long actualOrderLineItemId = actualOrderLineItemResponse.getId();
        Long actualOrderIdOfOrderLineItem = actualOrderLineItemResponse.getOrderId();
        Long actualMenuId = actualOrderLineItemResponse.getMenuId();
        Long expectationMenuId = menuId;
        Long actualQuantity = actualOrderLineItemResponse.getQuantity();
        Long expectationQuantity = orderLineItemRequest.getQuantity();
        Long i = null;
        assertAll(
                () -> assertThat(i).isEqualTo(null),
                () -> assertThat(actualOrderId).isEqualTo(actualOrderIdOfOrderLineItem),
                () -> assertThat(actualOrderTableId).isEqualTo(expectationOrderTableId),
                () -> assertThat(actualOrderStatus).isEqualTo(expectationOrderStatus),
                () -> assertThat(actualOrderedTime).isNotNull(),
                () -> assertThat(actualOrderLineItemId).isNotNull(),
                () -> assertThat(actualOrderIdOfOrderLineItem).isNotNull(),
                () -> assertThat(actualMenuId).isEqualTo(expectationMenuId),
                () -> assertThat(actualQuantity).isEqualTo(expectationQuantity)
        );
    }

    @Test
    void 주문_전체를_조회한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성();
        MenuGroup savedMenuGroup = jdbcTemplateMenuGroupDao.save(menuGroup);
        Menu menu = MENU_1.생성(savedMenuGroup);
        Long menuId = jdbcTemplateMenuDao.save(menu)
                .getId();
        OrderTable orderTable = ORDER_TABLE.생성();
        long orderTableId = jdbcTemplateOrderTable.save(orderTable)
                .getId();
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(menuId, 1L);
        List<OrderLineItemRequestToCreate> orderLineItemRequests = Collections.singletonList(orderLineItemRequest);
        OrderRequestToCreate orderRequest = new OrderRequestToCreate(orderTableId, orderLineItemRequests);
        ResponseEntity<OrderResponse> responseOfCreatedOrder = testRestTemplate.postForEntity(
                "/api/orders",
                orderRequest,
                OrderResponse.class
        );

        // when
        ResponseEntity<List<OrderResponse>> responseOfSelectedOrder = testRestTemplate.exchange(
                "/api/orders",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OrderResponse>>() {
                }
        );

        // then
        OrderResponse actualResponse = responseOfSelectedOrder.getBody()
                .get(0);
        OrderResponse expectationResponse = responseOfCreatedOrder.getBody();
        OrderLineItemResponse actualOrderLineItem = actualResponse.getOrderLineItems()
                .get(0);
        OrderLineItemResponse expectationOrderLineItem = expectationResponse.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(actualResponse).isEqualToIgnoringGivenFields(expectationResponse, "orderLineItems"),
                () -> assertThat(actualOrderLineItem).isEqualToComparingFieldByField(expectationOrderLineItem)
        );
    }

    @Test
    void 주문_상태를_변경한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성();
        MenuGroup savedMenuGroup = jdbcTemplateMenuGroupDao.save(menuGroup);
        Menu menu = MENU_1.생성(savedMenuGroup);
        Long menuId = jdbcTemplateMenuDao.save(menu)
                .getId();
        OrderTable orderTable = ORDER_TABLE.생성();
        long orderTableId = jdbcTemplateOrderTable.save(orderTable)
                .getId();
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(menuId, 1L);
        List<OrderLineItemRequestToCreate> orderLineItemRequests = Collections.singletonList(orderLineItemRequest);
        OrderRequestToCreate orderRequestToCreate = new OrderRequestToCreate(orderTableId, orderLineItemRequests);
        ResponseEntity<OrderResponse> orderResponseOfCreatedOrder = testRestTemplate.postForEntity(
                "/api/orders",
                orderRequestToCreate,
                OrderResponse.class
        );

        // when
        OrderResponse savedOrder = orderResponseOfCreatedOrder.getBody();
        Long orderId = savedOrder.getId();
        OrderRequestToChangeOrderStatus orderRequest = new OrderRequestToChangeOrderStatus(OrderStatus.MEAL.name());
        ResponseEntity<OrderResponse> orderResponse = testRestTemplate.exchange(
                "/api/orders/" + orderId + "/order-status",
                HttpMethod.PUT,
                new HttpEntity<>(orderRequest),
                OrderResponse.class
        );

        // then
        OrderResponse actualOrderResponse = orderResponse.getBody();
        Long actualOrderId = actualOrderResponse.getId();
        Long actualOrderTableId = actualOrderResponse.getOrderTableId();
        String actualOrderStatus = actualOrderResponse.getOrderStatus();
        String expectationOrderStatus = orderRequest.getOrderStatus();
        LocalDateTime actualOrderedTime = actualOrderResponse.getOrderedTime();
        LocalDateTime expectationOrderedTime = savedOrder.getOrderedTime();
        OrderLineItemResponse actualOrderLineItemResponse = actualOrderResponse.getOrderLineItems()
                .get(0);
        OrderLineItemResponse expectationOrderLineItemResponse = savedOrder.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(actualOrderId).isNotNull(),
                () -> assertThat(actualOrderTableId).isEqualTo(orderTableId),
                () -> assertThat(actualOrderStatus).isEqualTo(expectationOrderStatus),
                () -> assertThat(actualOrderedTime).isEqualTo(expectationOrderedTime),
                () -> assertThat(actualOrderLineItemResponse).isEqualToComparingFieldByField(
                        expectationOrderLineItemResponse)
        );
    }
}
