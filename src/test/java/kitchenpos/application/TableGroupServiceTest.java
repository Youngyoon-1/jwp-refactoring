package kitchenpos.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.TableGroup;
import kitchenpos.domain.TableGroupRepository;
import kitchenpos.domain.TableGroupValidator;
import kitchenpos.dto.request.TableGroupRequest;
import kitchenpos.dto.request.TableRequestToCreateTableGroup;
import kitchenpos.dto.response.OrderTableResponse;
import kitchenpos.dto.response.TableGroupResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableGroupServiceTest {

    @Mock
    private OrderTableRepository orderTableDao;

    @Mock
    private TableGroupRepository tableGroupDao;

    @Mock
    private TableGroupValidator tableGroupValidator;

    @InjectMocks
    private TableGroupService tableGroupService;

    @Test
    void 테이블_그룹을_생성한다() {
        // given
        BDDMockito.willDoNothing()
                .given(tableGroupValidator).validateToCreate(ArgumentMatchers.any(TableGroup.class));
        OrderTable orderTable1 = new OrderTable(1L, null, 0, true);
        OrderTable orderTable2 = new OrderTable(2L, null, 0, true);
        List<OrderTable> savedOrderTables = Arrays.asList(orderTable1, orderTable2);
        given(orderTableDao.findAllByIdIn(Arrays.asList(1L, 2L)))
                .willReturn(savedOrderTables);
        BDDMockito.willDoNothing()
                .given(tableGroupValidator)
                .validateToCreate(ArgumentMatchers.anyList(), ArgumentMatchers.eq(savedOrderTables));
        TableGroup savedTableGroup = new TableGroup(1L, LocalDateTime.now(), null);
        given(tableGroupDao.save(ArgumentMatchers.any(TableGroup.class)))
                .willReturn(savedTableGroup);

        // when
        TableRequestToCreateTableGroup tableRequest1 = new TableRequestToCreateTableGroup(1L);
        TableRequestToCreateTableGroup tableRequest2 = new TableRequestToCreateTableGroup(2L);
        TableGroupRequest tableGroupRequest = new TableGroupRequest(Arrays.asList(tableRequest1, tableRequest2));
        TableGroupResponse response = tableGroupService.create(tableGroupRequest);

        // then
        long tableGroupId = response.getId();
        LocalDateTime createdDate = response.getCreatedDate();
        OrderTableResponse orderTableResponse = response.getOrderTables()
                .get(0);
        long orderTableId = orderTableResponse.getId();
        long tableGroupIdOfOrderTable = orderTableResponse.getTableGroupId();
        int numberOfGuests = orderTableResponse.getNumberOfGuests();
        boolean empty = orderTableResponse.isEmpty();
        assertAll(
                () -> Assertions.assertThat(tableGroupId).isEqualTo(1L),
                () -> Assertions.assertThat(createdDate).isNotNull(),
                () -> Assertions.assertThat(orderTableId).isEqualTo(1L),
                () -> Assertions.assertThat(tableGroupIdOfOrderTable).isEqualTo(1L),
                () -> Assertions.assertThat(numberOfGuests).isZero(),
                () -> Assertions.assertThat(empty).isTrue(),
                () -> verify(tableGroupValidator).validateToCreate(ArgumentMatchers.any(TableGroup.class)),
                () -> verify(tableGroupValidator).validateToCreate(ArgumentMatchers.anyList(),
                        ArgumentMatchers.eq(savedOrderTables)),
                () -> verify(tableGroupDao).save(ArgumentMatchers.any(TableGroup.class))
        );
    }

    @Test
    void 테이블_그룹을_해제한다() {
        // given
        OrderTable savedOrderTable = new OrderTable(1L, 1L, 0, true);
        List<OrderTable> savedOrderTables = Collections.singletonList(savedOrderTable);
        given(orderTableDao.findAllByTableGroupId(1L))
                .willReturn(savedOrderTables);
        BDDMockito.willDoNothing()
                .given(tableGroupValidator).validateToUngroup(savedOrderTables);

        // when
        tableGroupService.ungroup(1L);

        // then
        Long tableGroupId = savedOrderTable.getTableGroupId();
        boolean empty = savedOrderTable.isEmpty();
        assertAll(
                () -> Assertions.assertThat(tableGroupId).isNull(),
                () -> Assertions.assertThat(empty).isFalse(),
                () -> verify(orderTableDao).findAllByTableGroupId(1L),
                () -> verify(tableGroupValidator).validateToUngroup(savedOrderTables)
        );
    }
}
