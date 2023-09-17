package kitchenpos.acceptance;

import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static kitchenpos.support.fixture.TableGroupFixture.TABLE_GROUP;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import kitchenpos.dao.JdbcTemplateOrderTableDao;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@AcceptanceTest
public class TableGroupAcceptanceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplateOrderTableDao jdbcTemplateOrderTableDao;

    @Test
    void 테이블_그룹을_생성한다() {
        // given
        OrderTable orderTable = ORDER_TABLE.생성(true);
        OrderTable savedOrderTable1 = jdbcTemplateOrderTableDao.save(orderTable);
        OrderTable savedOrderTable2 = jdbcTemplateOrderTableDao.save(orderTable);
        List<OrderTable> orderTables = Arrays.asList(savedOrderTable1, savedOrderTable2);

        TableGroup request = TABLE_GROUP.생성(orderTables);

        // when
        ResponseEntity<TableGroup> response = testRestTemplate.postForEntity(
                "/api/table-groups",
                request,
                TableGroup.class
        );

        // then
        List<OrderTable> actualOrderTables = request.getOrderTables();
        List<OrderTable> expectationOrderTables = response.getBody()
                .getOrderTables();
        assertThat(actualOrderTables).usingRecursiveComparison()
                .ignoringFields("tableGroupId", "empty")
                .isEqualTo(expectationOrderTables);
    }

    @Test
    void 테이블_그룹을_삭제한다() {
        OrderTable orderTable = ORDER_TABLE.생성(true);
        OrderTable savedOrderTable1 = jdbcTemplateOrderTableDao.save(orderTable);
        OrderTable savedOrderTable2 = jdbcTemplateOrderTableDao.save(orderTable);
        List<OrderTable> orderTables = Arrays.asList(savedOrderTable1, savedOrderTable2);
        TableGroup tableGroup = TABLE_GROUP.생성(orderTables);
        ResponseEntity<TableGroup> savedTableGroup = testRestTemplate.postForEntity(
                "/api/table-groups",
                tableGroup,
                TableGroup.class
        );

        // when
        long tableGroupId = savedTableGroup.getBody()
                .getId();
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/table-groups/" + tableGroupId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // then
        HttpStatus expectation = response.getStatusCode();
        assertThat(HttpStatus.NO_CONTENT).isEqualTo(expectation);
    }
}
