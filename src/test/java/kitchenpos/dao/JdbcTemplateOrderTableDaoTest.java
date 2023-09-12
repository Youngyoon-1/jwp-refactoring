package kitchenpos.dao;

import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE_1;
import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE_2;
import static kitchenpos.support.fixture.TableGroupFixture.TABLE_GROUP_1;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

@JdbcTest
class JdbcTemplateOrderTableDaoTest {

    private final JdbcTemplateOrderTableDao jdbcTemplateOrderTableDao;
    private final JdbcTemplateTableGroupDao jdbcTemplateTableGroupDao;

    @Autowired
    private JdbcTemplateOrderTableDaoTest(final DataSource dataSource) {
        this.jdbcTemplateOrderTableDao = new JdbcTemplateOrderTableDao(dataSource);
        this.jdbcTemplateTableGroupDao = new JdbcTemplateTableGroupDao(dataSource);
    }

    @Test
    void 새로운_주문_테이블을_저장한다() {
        // given
        OrderTable orderTable = ORDER_TABLE_1.손님_한_명_테이블_그룹_없이_생성();

        // when
        OrderTable savedOrderTable = jdbcTemplateOrderTableDao.save(orderTable);

        // then
        assertThat(orderTable).isEqualToIgnoringGivenFields(savedOrderTable, "id");
    }

    @Test
    void 저장된_주문_테이블의_테이블_그룹과_손님_수와_빈자리_여부를_수정한다() {
        // given
        TableGroup savedTableGroup = 테이블_그룹_저장(TABLE_GROUP_1.생성());
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE_1.손님_한_명_테이블_그룹과_생성(savedTableGroup));
        savedOrderTable.setEmpty(true);
        savedOrderTable.setNumberOfGuests(0);
        savedOrderTable.setTableGroupId(null);

        // when
        OrderTable updatedOrderTable = jdbcTemplateOrderTableDao.save(savedOrderTable);

        // then
        assertThat(savedOrderTable).isEqualToComparingFieldByField(updatedOrderTable);
    }

    @Test
    void 주문_테이블을_ID_로_조회한다() {
        // given
        OrderTable savedOrderTable = 주문_테이블_저장(ORDER_TABLE_1.손님_한_명_테이블_그룹_없이_생성());
        long id = savedOrderTable.getId();

        // when
        OrderTable selectedOrderTable = jdbcTemplateOrderTableDao.findById(id)
                .get();

        // then
        assertThat(savedOrderTable).isEqualToComparingFieldByField(selectedOrderTable);
    }

    @Test
    void 주문_테이블_전체를_조회한다() {
        //given
        OrderTable savedOrderTable1 = 주문_테이블_저장(ORDER_TABLE_1.손님_한_명_테이블_그룹_없이_생성());
        OrderTable savedOrderTable2 = 주문_테이블_저장(ORDER_TABLE_2.손님_한_명_테이블_그룹_없이_생성());
        List<OrderTable> savedOrderTables = new ArrayList<>();
        savedOrderTables.add(savedOrderTable1);
        savedOrderTables.add(savedOrderTable2);

        // when
        List<OrderTable> selectedOrderTables = jdbcTemplateOrderTableDao.findAll();

        // then
        assertThat(savedOrderTables).usingRecursiveComparison()
                .isEqualTo(selectedOrderTables);
    }

    @Test
    void 주문_테이블을_N_개의_ID_로_조회한다() {
        // given
        OrderTable savedOrderTable1 = 주문_테이블_저장(ORDER_TABLE_1.손님_한_명_테이블_그룹_없이_생성());
        OrderTable savedOrderTable2 = 주문_테이블_저장(ORDER_TABLE_2.손님_한_명_테이블_그룹_없이_생성());
        List<OrderTable> savedOrderTables = new ArrayList<>();
        savedOrderTables.add(savedOrderTable1);
        savedOrderTables.add(savedOrderTable2);
        List<Long> ids = new ArrayList<>();
        ids.add(savedOrderTable1.getId());
        ids.add(savedOrderTable2.getId());

        // when
        List<OrderTable> selectedOrderTables = jdbcTemplateOrderTableDao.findAllByIdIn(ids);

        // then
        assertThat(savedOrderTables).usingRecursiveComparison()
                .isEqualTo(selectedOrderTables);
    }

    @Test
    void 주문_테이블을_테이블_그룹_ID_로_조회한다() {
        // given
        TableGroup savedTableGroup = 테이블_그룹_저장(TABLE_GROUP_1.생성());
        OrderTable savedOrderTable1 = 주문_테이블_저장(ORDER_TABLE_1.손님_한_명_테이블_그룹과_생성(savedTableGroup));
        OrderTable savedOrderTable2 = 주문_테이블_저장(ORDER_TABLE_2.손님_한_명_테이블_그룹과_생성(savedTableGroup));
        List<OrderTable> savedOrderTables = new ArrayList<>();
        savedOrderTables.add(savedOrderTable1);
        savedOrderTables.add(savedOrderTable2);
        long id = savedTableGroup.getId();

        // when
        List<OrderTable> selectedOrderTables = jdbcTemplateOrderTableDao.findAllByTableGroupId(id);

        // then
        assertThat(savedOrderTables).usingRecursiveComparison()
                .isEqualTo(selectedOrderTables);
    }

    private OrderTable 주문_테이블_저장(final OrderTable orderTable) {
        return jdbcTemplateOrderTableDao.save(orderTable);
    }

    private TableGroup 테이블_그룹_저장(final TableGroup tableGroup) {
        return jdbcTemplateTableGroupDao.save(tableGroup);
    }
}
