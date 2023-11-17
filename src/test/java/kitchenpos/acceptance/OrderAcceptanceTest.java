package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
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
    private OrderTableRepository orderTableRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void 주문_한_개를_등록한다() {
        // given
        MenuGroup menuGroup = new MenuGroup("메뉴그룹이름");
        menuGroupRepository.save(menuGroup);
        Menu menu = new Menu("메뉴이름", BigDecimal.valueOf(1000), menuGroup.getId(), null);
        menuRepository.save(menu);
        OrderTable orderTable = new OrderTable(null, null, 1, false);
        orderTableRepository.save(orderTable);
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(menu.getId(), 1L);
        OrderRequestToCreate request = new OrderRequestToCreate(orderTable.getId(),
                Collections.singletonList(orderLineItemRequest));

        // when
        ResponseEntity<OrderResponse> response = testRestTemplate.postForEntity(
                "/api/orders",
                request,
                OrderResponse.class
        );

        // then
        OrderResponse orderResponse = response.getBody();
        Long orderId = orderResponse.getId();
        Long orderTableId = orderResponse.getOrderTableId();
        Long expectationOrderTableId = orderTable.getId();
        String orderStatus = orderResponse.getOrderStatus();
        LocalDateTime orderedTime = orderResponse.getOrderedTime();
        OrderLineItemResponse orderLineItemResponse = orderResponse.getOrderLineItems()
                .get(0);
        Long orderLineItemId = orderLineItemResponse.getSeq();
        Long orderIdOfOrderLineItem = orderLineItemResponse.getOrderId();
        Long menuId = orderLineItemResponse.getMenuId();
        Long expectationMenuId = menu.getId();
        Long quantity = orderLineItemResponse.getQuantity();
        Long expectationQuantity = orderLineItemRequest.getQuantity();
        assertAll(
                () -> assertThat(orderId).isNotNull(),
                () -> assertThat(orderTableId).isEqualTo(expectationOrderTableId),
                () -> assertThat(orderStatus).isEqualTo(OrderStatus.COOKING.name()),
                () -> assertThat(orderedTime).isNotNull(),
                () -> assertThat(orderLineItemId).isNotNull(),
                () -> assertThat(orderIdOfOrderLineItem).isEqualTo(orderId),
                () -> assertThat(menuId).isEqualTo(expectationMenuId),
                () -> assertThat(quantity).isEqualTo(expectationQuantity)
        );
    }

    @Test
    void 주문_한_개를_등록하고_전체를_조회한다() {
        // given
        MenuGroup menuGroup = new MenuGroup("메뉴그룹이름");
        menuGroupRepository.save(menuGroup);
        Menu menu = new Menu("메뉴이름", BigDecimal.valueOf(1000), menuGroup.getId(), null);
        menuRepository.save(menu);
        OrderTable orderTable = new OrderTable(null, null, 1, false);
        orderTableRepository.save(orderTable);
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(menu.getId(), 1L);
        OrderRequestToCreate request = new OrderRequestToCreate(orderTable.getId(),
                Collections.singletonList(orderLineItemRequest));

        // when
        ResponseEntity<OrderResponse> responseToSave = testRestTemplate.postForEntity(
                "/api/orders",
                request,
                OrderResponse.class
        );
        ResponseEntity<List<OrderResponse>> responseToSelect = testRestTemplate.exchange(
                "/api/orders",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OrderResponse>>() {
                }
        );

        // then
        OrderResponse orderResponseToSave = responseToSave.getBody();
        OrderResponse orderResponseToSelect = responseToSelect.getBody()
                .get(0);
        OrderLineItemResponse orderLineItemResponseToSave = orderResponseToSave.getOrderLineItems()
                .get(0);
        OrderLineItemResponse orderLineItemResponseToSelect = orderResponseToSelect.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(orderResponseToSave).isEqualToIgnoringGivenFields(orderResponseToSelect,
                        "orderLineItems"),
                () -> assertThat(orderLineItemResponseToSave).isEqualToComparingFieldByField(
                        orderLineItemResponseToSelect)
        );
    }

    @Test
    void 주문을_한_개_저장하고_그_주문의_상태를_식사로_변경한다() {
        // given
        MenuGroup menuGroup = new MenuGroup("메뉴그룹이름");
        menuGroupRepository.save(menuGroup);
        Menu menu = new Menu("메뉴이름", BigDecimal.valueOf(1000), menuGroup.getId(), null);
        menuRepository.save(menu);
        OrderTable orderTable = new OrderTable(null, null, 1, false);
        orderTableRepository.save(orderTable);
        OrderLineItemRequestToCreate orderLineItemRequest = new OrderLineItemRequestToCreate(menu.getId(), 1L);
        OrderRequestToCreate requestToSave = new OrderRequestToCreate(orderTable.getId(),
                Collections.singletonList(orderLineItemRequest));

        // when
        ResponseEntity<OrderResponse> responseToSave = testRestTemplate.postForEntity(
                "/api/orders",
                requestToSave,
                OrderResponse.class
        );
        OrderResponse orderResponseToSave = responseToSave.getBody();
        long orderId = orderResponseToSave.getId();
        OrderRequestToChangeOrderStatus requestToChange = new OrderRequestToChangeOrderStatus(OrderStatus.MEAL.name());
        ResponseEntity<OrderResponse> responseToChange = testRestTemplate.exchange(
                "/api/orders/" + orderId + "/order-status",
                HttpMethod.PUT,
                new HttpEntity<>(requestToChange),
                OrderResponse.class
        );

        // then
        OrderResponse orderResponseToChange = responseToChange.getBody();
        String orderStatus = orderResponseToChange.getOrderStatus();
        OrderLineItemResponse orderLineItemToSave = orderResponseToSave.getOrderLineItems()
                .get(0);
        OrderLineItemResponse orderLineItemToChange = orderResponseToChange.getOrderLineItems()
                .get(0);
        assertAll(
                () -> assertThat(orderResponseToSave).isEqualToIgnoringGivenFields(orderResponseToChange,
                        "orderStatus", "orderLineItems"),
                () -> assertThat(orderStatus).isEqualTo(OrderStatus.MEAL.name()),
                () -> assertThat(orderLineItemToSave).isEqualToComparingFieldByField(orderLineItemToChange)
        );
    }
}
