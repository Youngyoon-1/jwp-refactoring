package kitchenpos.dao;

import static kitchenpos.support.fixture.MenuFixture.MENU_1;
import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static kitchenpos.support.fixture.OrderFixture.ORDER;
import static kitchenpos.support.fixture.OrderLineItemFixture.ORDER_LINE_ITEM_1;
import static kitchenpos.support.fixture.OrderLineItemFixture.ORDER_LINE_ITEM_2;
import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

@JdbcTest
class JdbcTemplateOrderLineItemDaoTest {

    private final JdbcTemplateOrderLineItemDao jdbcTemplateOrderLineItemDao;
    private final JdbcTemplateOrderDao jdbcTemplateOrderDao;
    private final JdbcTemplateOrderTableDao jdbcTemplateOrderTableDao;
    private final JdbcTemplateMenuDao jdbcTemplateMenuDao;
    private final JdbcTemplateMenuGroupDao jdbcTemplateMenuGroupDao;

    @Autowired
    private JdbcTemplateOrderLineItemDaoTest(final DataSource dataSource) {
        this.jdbcTemplateOrderLineItemDao = new JdbcTemplateOrderLineItemDao(dataSource);
        this.jdbcTemplateOrderDao = new JdbcTemplateOrderDao(dataSource);
        this.jdbcTemplateOrderTableDao = new JdbcTemplateOrderTableDao(dataSource);
        this.jdbcTemplateMenuDao = new JdbcTemplateMenuDao(dataSource);
        this.jdbcTemplateMenuGroupDao = new JdbcTemplateMenuGroupDao(dataSource);
    }

    @Test
    void 주문_항목을_저장한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order savedOrder = 주문_저장(ORDER.조리_상태로_생성(savedOrderTable));
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        OrderLineItem orderLineItem = ORDER_LINE_ITEM_1.생성(savedOrder, savedMenu);

        // when
        OrderLineItem savedOrderLineItem = jdbcTemplateOrderLineItemDao.save(orderLineItem);

        // then
        assertThat(orderLineItem).isEqualToIgnoringGivenFields(savedOrderLineItem, "seq");
    }

    @Test
    void 주문_항목을_ID_로_조회한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order savedOrder = 주문_저장(ORDER.조리_상태로_생성(savedOrderTable));
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        OrderLineItem savedOrderLineItem = 주문_항목_저장(ORDER_LINE_ITEM_1.생성(savedOrder, savedMenu));
        long id = savedOrderLineItem.getSeq();

        // when
        OrderLineItem selectedOrderLineItem = jdbcTemplateOrderLineItemDao.findById(id)
                .get();

        // then
        assertThat(savedOrderLineItem).isEqualToComparingFieldByField(selectedOrderLineItem);
    }

    @Test
    void 주문_항목_전체를_조회한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order savedOrder = 주문_저장(ORDER.조리_상태로_생성(savedOrderTable));
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        OrderLineItem savedOrderLineItem1 = 주문_항목_저장(ORDER_LINE_ITEM_1.생성(savedOrder, savedMenu));
        OrderLineItem savedOrderLineItem2 = 주문_항목_저장(ORDER_LINE_ITEM_2.생성(savedOrder, savedMenu));
        List<OrderLineItem> savedOrderLineItems = new ArrayList<>();
        savedOrderLineItems.add(savedOrderLineItem1);
        savedOrderLineItems.add(savedOrderLineItem2);

        // when
        List<OrderLineItem> selectedOrderLineItems = jdbcTemplateOrderLineItemDao.findAll();

        // then
        assertThat(savedOrderLineItems).usingRecursiveComparison()
                .isEqualTo(selectedOrderLineItems);
    }


    @Test
    void 주문_항목을_주문_ID_로_조회한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE.손님_한_명_테이블_그룹_없이_생성());
        Order savedOrder = 주문_저장(ORDER.조리_상태로_생성(savedOrderTable));
        MenuGroup savedMenuGroup = 메뉴_그룹_저장(MENU_GROUP_1.생성());
        Menu savedMenu = 메뉴_저장(MENU_1.생성(savedMenuGroup));
        OrderLineItem savedOrderLineItem1 = 주문_항목_저장(ORDER_LINE_ITEM_1.생성(savedOrder, savedMenu));
        OrderLineItem savedOrderLineItem2 = 주문_항목_저장(ORDER_LINE_ITEM_2.생성(savedOrder, savedMenu));
        List<OrderLineItem> savedOrderLineItems = new ArrayList<>();
        savedOrderLineItems.add(savedOrderLineItem1);
        savedOrderLineItems.add(savedOrderLineItem2);
        long id = savedOrder.getId();

        // when
        List<OrderLineItem> selectedOrderLineItem = jdbcTemplateOrderLineItemDao.findAllByOrderId(id);

        // then
        assertThat(savedOrderLineItems).usingRecursiveComparison()
                .isEqualTo(selectedOrderLineItem);
    }

    private OrderLineItem 주문_항목_저장(final OrderLineItem orderLineItem) {
        return jdbcTemplateOrderLineItemDao.save(orderLineItem);
    }

    private Menu 메뉴_저장(final Menu menu) {
        return jdbcTemplateMenuDao.save(menu);
    }

    private Order 주문_저장(final Order order) {
        return jdbcTemplateOrderDao.save(order);
    }

    private MenuGroup 메뉴_그룹_저장(final MenuGroup menuGroup) {
        return jdbcTemplateMenuGroupDao.save(menuGroup);
    }

    private OrderTable 주문_테이블_저장(final OrderTable orderTable) {
        return jdbcTemplateOrderTableDao.save(orderTable);
    }
}
