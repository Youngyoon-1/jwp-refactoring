package kitchenpos.acceptance;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.dto.request.TableGroupRequest;
import kitchenpos.dto.request.TableRequestToCreateTableGroup;
import kitchenpos.dto.response.OrderTableResponse;
import kitchenpos.dto.response.TableGroupResponse;
import org.assertj.core.api.Assertions;
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
    private OrderTableRepository orderTableRepository;

    @Test
    void 테이블_그룹을_생성한다() {
        // given
        long orderTable1Id = orderTableRepository.save(new OrderTable(null, null, 0, true))
                .getId();
        long orderTable2Id = orderTableRepository.save(new OrderTable(null, null, 0, true))
                .getId();
        List<TableRequestToCreateTableGroup> tableRequests = Arrays.asList(
                new TableRequestToCreateTableGroup(orderTable1Id),
                new TableRequestToCreateTableGroup(orderTable2Id));
        TableGroupRequest request = new TableGroupRequest(tableRequests);

        // when
        ResponseEntity<TableGroupResponse> response = testRestTemplate.postForEntity(
                "/api/table-groups",
                request,
                TableGroupResponse.class
        );

        // then
        TableGroupResponse tableGroupResponse = response.getBody();
        long actualOrderGroupId = tableGroupResponse.getId();
        LocalDateTime actualCreatedDate = tableGroupResponse.getCreatedDate();
        List<OrderTableResponse> actualOrderTables = tableGroupResponse.getOrderTables();
        OrderTableResponse actualOrderTable1 = actualOrderTables.get(0);
        long actualOrderTable1Id = actualOrderTable1.getId();
        long actualOrderTable1GroupId = actualOrderTable1.getTableGroupId();
        long actualOrderTable1NumberOfGuests = actualOrderTable1.getNumberOfGuests();
        boolean actualOrderTable1Empty = actualOrderTable1.isEmpty();
        OrderTableResponse actualOrderTable2 = actualOrderTables.get(1);
        long actualOrderTable2Id = actualOrderTable2.getId();
        long actualOrderTable2GroupId = actualOrderTable2.getTableGroupId();
        long actualOrderTable2NumberOfGuests = actualOrderTable2.getNumberOfGuests();
        boolean actualOrderTable2Empty = actualOrderTable2.isEmpty();

        assertAll(
                () -> Assertions.assertThat(actualOrderGroupId).isNotNull(),
                () -> Assertions.assertThat(actualCreatedDate).isNotNull(),
                () -> Assertions.assertThat(actualOrderTable1Id).isNotNull(),
                () -> Assertions.assertThat(actualOrderTable1GroupId).isEqualTo(actualOrderGroupId),
                () -> Assertions.assertThat(actualOrderTable1NumberOfGuests).isZero(),
                () -> Assertions.assertThat(actualOrderTable1Empty).isTrue(),
                () -> Assertions.assertThat(actualOrderTable2Id).isNotNull(),
                () -> Assertions.assertThat(actualOrderTable2GroupId).isEqualTo(actualOrderGroupId),
                () -> Assertions.assertThat(actualOrderTable2NumberOfGuests).isZero(),
                () -> Assertions.assertThat(actualOrderTable2Empty).isTrue()
        );
    }

    @Test
    void 테이블_그룹을_저장한_뒤_삭제한다() {
        // given
        long orderTable1Id = orderTableRepository.save(new OrderTable(null, null, 0, true))
                .getId();
        long orderTable2Id = orderTableRepository.save(new OrderTable(null, null, 0, true))
                .getId();
        List<TableRequestToCreateTableGroup> tableRequests = Arrays.asList(
                new TableRequestToCreateTableGroup(orderTable1Id),
                new TableRequestToCreateTableGroup(orderTable2Id));
        TableGroupRequest requestToSave = new TableGroupRequest(tableRequests);

        // when
        ResponseEntity<TableGroupResponse> responseToSave = testRestTemplate.postForEntity(
                "/api/table-groups",
                requestToSave,
                TableGroupResponse.class
        );
        TableGroupResponse tableGroupResponseToSave = responseToSave.getBody();
        long tableGroupId = tableGroupResponseToSave.getId();
        ResponseEntity<Void> responseToDelete = testRestTemplate.exchange(
                "/api/table-groups/" + tableGroupId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // then
        HttpStatus httpStatusToSave = responseToSave.getStatusCode();
        HttpStatus httpStatusToDelete = responseToDelete.getStatusCode();
        assertAll(
                () -> Assertions.assertThat(httpStatusToSave).isEqualTo(HttpStatus.CREATED),
                () -> Assertions.assertThat(httpStatusToDelete).isEqualTo(HttpStatus.NO_CONTENT)
        );
    }
}
