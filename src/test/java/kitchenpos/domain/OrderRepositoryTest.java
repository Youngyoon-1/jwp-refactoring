package kitchenpos.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderLineItemRepository orderLineItemRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 주어진_주문_테이블_id_와_n_개의_주문_상태에_부합하는_주문이_하나라도_존재하는지_조회한다() {
        // given
        long orderTableId = orderTableRepository.save(new OrderTable())
                .getId();
        List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());
        orderRepository.save(new Order(null, orderTableId, orderStatuses.get(0), LocalDateTime.now(), null));
        orderRepository.save(new Order(null, orderTableId, orderStatuses.get(1), LocalDateTime.now(), null));

        // when
        boolean existence = orderRepository.existsByOrderTableIdAndOrderStatusIn(orderTableId, orderStatuses);

        // then
        Assertions.assertThat(existence).isTrue();
    }

    @Test
    void 주어진_n_개의_주문_테이블_id_와_n_개의_주문_상태에_부합하는_주문이_하나라도_존재하는지_조회한다() {
        // given
        long orderTableId1 = orderTableRepository.save(new OrderTable())
                .getId();
        long orderTableId2 = orderTableRepository.save(new OrderTable())
                .getId();
        List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());
        orderRepository.save(new Order(null, orderTableId1, orderStatuses.get(0), LocalDateTime.now(), null));
        orderRepository.save(new Order(null, orderTableId2, orderStatuses.get(1), LocalDateTime.now(), null));

        // when
        boolean existence = orderRepository.existsByOrderTableIdInAndOrderStatusIn(
                Arrays.asList(orderTableId1, orderTableId2), orderStatuses);

        // then
        Assertions.assertThat(existence).isTrue();
    }

    @Test
    void findAll_의_N_플러스_1_문제_해결() {
        // given
        long orderTableId = orderTableRepository.save(new OrderTable())
                .getId();
        long orderId = orderRepository.save(
                        new Order(null, orderTableId, OrderStatus.COOKING.name(), LocalDateTime.now(), null))
                .getId();
        long menuGroupId = menuGroupRepository.save(new MenuGroup("메뉴그룹"))
                .getId();
        long menuId1 = menuRepository.save(new Menu("메뉴", BigDecimal.valueOf(500), menuGroupId, null))
                .getId();
        long menuId2 = menuRepository.save(new Menu("메뉴", BigDecimal.valueOf(500), menuGroupId, null))
                .getId();
        orderLineItemRepository.save(new OrderLineItem(null, orderId, menuId1, 1L));
        orderLineItemRepository.save(new OrderLineItem(null, orderId, menuId2, 1L));
        entityManager.clear();

        // when
        List<Order> totalOrder = orderRepository.findAll();

        // then
        Assertions.assertThat(totalOrder.size()).isEqualTo(1);
    }
}
