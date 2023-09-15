package kitchenpos.dao;

import static kitchenpos.support.fixture.OrderFixture.ORDER;
import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.support.fixture.OrderFixture;
import kitchenpos.support.fixture.OrderTableFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

@JdbcTest
class JdbcTemplateOrderDaoTest {

    private final JdbcTemplateOrderDao jdbcTemplateOrderDao;
    private final JdbcTemplateOrderTableDao jdbcTemplateOrderTableDao;

    @Autowired
    private JdbcTemplateOrderDaoTest(final DataSource dataSource) {
        this.jdbcTemplateOrderDao = new JdbcTemplateOrderDao(dataSource);
        this.jdbcTemplateOrderTableDao = new JdbcTemplateOrderTableDao(dataSource);
    }

    @Test
    void 주문을_저장한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order order = ORDER.조리_상태로_생성(savedOrderTable);

        // when
        Order savedOrder = jdbcTemplateOrderDao.save(order);

        // then
        assertThat(order).isEqualToIgnoringGivenFields(savedOrder, "id");
    }

    @Test
    void 저장된_주문의_상태를_수정한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order savedOrder = 주문_저장(ORDER.조리_상태로_생성(savedOrderTable));
        savedOrder.setOrderStatus(OrderStatus.COOKING.toString());

        // when
        Order updatedOrder = jdbcTemplateOrderDao.save(savedOrder);

        // then
        assertThat(savedOrder).isEqualToComparingFieldByField(updatedOrder);
    }


    @Test
    void 주문을_ID_로_조회한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order savedOrder = 주문_저장(ORDER.조리_상태로_생성(savedOrderTable));
        long id = savedOrder.getId();

        // when
        Order selectedOrder = jdbcTemplateOrderDao.findById(id)
                .get();

        // then
        assertThat(savedOrder).isEqualToComparingFieldByField(selectedOrder);
    }

    @Test
    void 주문을_전체_조회한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order savedOrder1 = 주문_저장(ORDER.조리_상태로_생성(savedOrderTable));
        Order savedOrder2 = 주문_저장(OrderFixture.ORDER.조리_상태로_생성(savedOrderTable));
        List<Order> savedOrders = new ArrayList<>();
        savedOrders.add(savedOrder1);
        savedOrders.add(savedOrder2);

        // when
        List<Order> selectedOrders = jdbcTemplateOrderDao.findAll();

        // then
        assertThat(savedOrders).usingRecursiveComparison()
                .isEqualTo(selectedOrders);
    }

    @Test
    void 주문의_존재_여부를_주문_테이블_ID_와_N_개의_주문_상태로_조회한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order savedOrder1 = 주문_저장(ORDER.조리_상태로_생성(savedOrderTable));
        Order savedOrder2 = 주문_저장(OrderFixture.ORDER.식사_상태로_생성(savedOrderTable));
        long id = savedOrderTable.getId();
        List<String> orderStatuses = new ArrayList<>();
        orderStatuses.add(savedOrder1.getOrderStatus());
        orderStatuses.add(savedOrder2.getOrderStatus());

        // when
        boolean expectation = jdbcTemplateOrderDao.existsByOrderTableIdAndOrderStatusIn(id, orderStatuses);

        // then
        assertThat(expectation).isTrue();
    }

    @Test
    void 주문의_존재_여부를_N_개의_주문_테이블_ID_와_N_개의_주문_상태로_조회한다() {
        // given
        OrderTable savedOrderTable1 = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        OrderTable savedOrderTable2 = 주문_테이블_저장(OrderTableFixture.ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order savedOrder1 = 주문_저장(ORDER.조리_상태로_생성(savedOrderTable1));
        Order savedOrder2 = 주문_저장(OrderFixture.ORDER.식사_상태로_생성(savedOrderTable2));
        List<Long> ids = new ArrayList<>();
        ids.add(savedOrder1.getOrderTableId());
        ids.add(savedOrder2.getOrderTableId());
        List<String> orderStatuses = new ArrayList<>();
        orderStatuses.add(savedOrder1.getOrderStatus());
        orderStatuses.add(savedOrder2.getOrderStatus());

        // when
        boolean expectation = jdbcTemplateOrderDao.existsByOrderTableIdInAndOrderStatusIn(ids, orderStatuses);

        // then
        assertThat(expectation).isTrue();
    }

    private Order 주문_저장(final Order order) {
        return jdbcTemplateOrderDao.save(order);
    }

    private OrderTable 주문_테이블_저장(final OrderTable orderTable) {
        return jdbcTemplateOrderTableDao.save(orderTable);
    }
}