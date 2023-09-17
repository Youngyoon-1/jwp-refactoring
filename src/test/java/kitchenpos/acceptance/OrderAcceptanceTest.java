package kitchenpos.acceptance;

import static kitchenpos.domain.OrderStatus.MEAL;
import static kitchenpos.support.fixture.MenuFixture.MENU_1;
import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static kitchenpos.support.fixture.OrderFixture.ORDER;
import static kitchenpos.support.fixture.OrderLineItemFixture.ORDER_LINE_ITEM_1;
import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import kitchenpos.dao.JdbcTemplateMenuDao;
import kitchenpos.dao.JdbcTemplateMenuGroupDao;
import kitchenpos.dao.JdbcTemplateOrderTableDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
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
        Menu savedMenu = jdbcTemplateMenuDao.save(menu);
        OrderLineItem orderLineItem = ORDER_LINE_ITEM_1.생성(savedMenu);
        List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItem);
        OrderTable orderTable = ORDER_TABLE.생성();
        long orderTableId = jdbcTemplateOrderTable.save(orderTable)
                .getId();
        Order request = ORDER.생성(orderTableId, orderLineItems);

        // when
        ResponseEntity<Order> response = testRestTemplate.postForEntity(
                "/api/orders",
                request,
                Order.class
        );

        // then
        Order actual = request;
        OrderLineItem actualOrderLineItem = actual.getOrderLineItems()
                .get(0);
        Order expectation = response.getBody();
        assert expectation != null;
        OrderLineItem expectationOrderLineItem = expectation.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(actual).isEqualToComparingOnlyGivenFields(expectation, "orderTableId"),
                () -> assertThat(actualOrderLineItem).isEqualToComparingOnlyGivenFields(expectationOrderLineItem,
                        "menuId", "quantity")
        );
    }

    @Test
    void 주문_전체를_조회한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성();
        MenuGroup savedMenuGroup = jdbcTemplateMenuGroupDao.save(menuGroup);
        Menu menu = MENU_1.생성(savedMenuGroup);
        Menu savedMenu = jdbcTemplateMenuDao.save(menu);
        OrderLineItem orderLineItem = ORDER_LINE_ITEM_1.생성(savedMenu);
        List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItem);
        OrderTable orderTable = ORDER_TABLE.생성();
        long orderTableId = jdbcTemplateOrderTable.save(orderTable)
                .getId();
        Order request = ORDER.생성(orderTableId, orderLineItems);
        ResponseEntity<Order> postResponse = testRestTemplate.postForEntity(
                "/api/orders",
                request,
                Order.class
        );

        // when
        ResponseEntity<List<Order>> response = testRestTemplate.exchange(
                "/api/orders",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Order>>() {
                }
        );

        // then
        Order actual = postResponse.getBody();
        assert actual != null;
        OrderLineItem actualOrderLineItem = actual.getOrderLineItems()
                .get(0);
        Order expectation = Objects.requireNonNull(response.getBody())
                .get(0);
        OrderLineItem expectationOrderLineItem = expectation.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(actual).isEqualToIgnoringGivenFields(expectation, "orderLineItems"),
                () -> assertThat(actualOrderLineItem).isEqualToComparingFieldByField(expectationOrderLineItem)
        );
    }

    @Test
    void 주문_상태를_변경한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성();
        MenuGroup savedMenuGroup = jdbcTemplateMenuGroupDao.save(menuGroup);
        Menu menu = MENU_1.생성(savedMenuGroup);
        Menu savedMenu = jdbcTemplateMenuDao.save(menu);
        OrderLineItem orderLineItem = ORDER_LINE_ITEM_1.생성(savedMenu);
        List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItem);
        OrderTable orderTable = ORDER_TABLE.생성();
        long orderTableId = jdbcTemplateOrderTable.save(orderTable)
                .getId();
        Order requestToCreateOrder = ORDER.생성(orderTableId, orderLineItems);
        ResponseEntity<Order> responseToCreateOrder = testRestTemplate.postForEntity(
                "/api/orders",
                requestToCreateOrder,
                Order.class
        );

        // when
        Long orderIdRequest = responseToCreateOrder.getBody()
                .getId();
        Order request = ORDER.생성(MEAL.toString());
        ResponseEntity<Order> response = testRestTemplate.exchange(
                "/api/orders/"
                        + orderIdRequest
                        + "/order-status",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                Order.class
        );

        // then
        Order actual = responseToCreateOrder.getBody();
        String actualOrderStatus = request.getOrderStatus();
        OrderLineItem actualOrderLineItem = actual.getOrderLineItems()
                .get(0);
        Order expectation = response.getBody();
        String expectationOrderStatus = expectation.getOrderStatus();
        OrderLineItem expectationOrderLineItem = expectation.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(actual).isEqualToIgnoringGivenFields(expectation, "orderStatus", "orderLineItems"),
                () -> assertThat(actualOrderStatus).isEqualTo(expectationOrderStatus),
                () -> assertThat(actualOrderLineItem).isEqualToComparingFieldByField(expectationOrderLineItem)
        );
    }
}
