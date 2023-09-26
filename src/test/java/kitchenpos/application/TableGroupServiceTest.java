package kitchenpos.application;

import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.request.TableGroupRequest;
import kitchenpos.dto.request.TableRequestToCreateTableGroup;
import kitchenpos.dto.response.OrderTableResponse;
import kitchenpos.dto.response.TableGroupResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableGroupServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupService tableGroupService;

    @Test
    void 테이블_그룹을_생성한다() {
        // given
        Long orderTableId1 = 1L;
        Long orderTableId2 = 2L;
        List<Long> orderTableIds = Arrays.asList(orderTableId1, orderTableId2);
        OrderTable orderTable1 = new OrderTable(orderTableId1, null, 0, true);
        OrderTable orderTable2 = new OrderTable(orderTableId2, null, 0, true);
        List<OrderTable> savedOrderTables = Arrays.asList(orderTable1, orderTable2);
        given(orderTableDao.findAllByIdIn(orderTableIds))
                .willReturn(savedOrderTables);
        TableRequestToCreateTableGroup tableRequest1 = new TableRequestToCreateTableGroup(orderTableId1);
        TableRequestToCreateTableGroup tableRequest2 = new TableRequestToCreateTableGroup(orderTableId2);
        TableGroupRequest tableGroupRequest = new TableGroupRequest(Arrays.asList(tableRequest1, tableRequest2));
        LocalDateTime createdDate = tableGroupRequest.toEntity()
                .getCreatedDate();
        TableGroup savedTableGroup = new TableGroup(1L, createdDate, null);
        given(tableGroupDao.save(ArgumentMatchers.any(TableGroup.class)))
                .willReturn(savedTableGroup);
        given(orderTableDao.save(ArgumentMatchers.any(OrderTable.class)))
                .willReturn(null);

        // when
        TableGroupResponse tableGroupResponse = tableGroupService.create(tableGroupRequest);

        // then
        List<OrderTableResponse> actualOrderTables = tableGroupResponse.getOrderTables();
        List<OrderTable> expectationOrderTables = savedOrderTables;
        assertAll(
                () -> assertThat(actualOrderTables).usingRecursiveComparison()
                        .isEqualTo(expectationOrderTables),
                () -> verify(orderTableDao).findAllByIdIn(orderTableIds),
                () -> verify(tableGroupDao).save(ArgumentMatchers.any(TableGroup.class)),
                () -> verify(orderTableDao, times(2)).save(ArgumentMatchers.any(OrderTable.class))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void 테이블_그룹을_생성할_때_주문_테이블이_0개_또는_2개_미만인_경우_예외가_발생한다(final int count) {
        List<TableRequestToCreateTableGroup> orderTableRequests = new ArrayList<>();
        TableRequestToCreateTableGroup orderTableRequest = new TableRequestToCreateTableGroup((long) count);
        for (int i = 0; i < count; i++) {
            orderTableRequests.add(orderTableRequest);
        }
        TableGroupRequest tableGroupRequest = new TableGroupRequest(orderTableRequests);
        assertThatThrownBy(() -> tableGroupService.create(tableGroupRequest))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 테이블_그룹을_생성할_때_등록되지_않은_주문_테이블이_존재하면_예외가_발생한다() {
        // given
        Long orderTableId1 = 1L;
        Long orderTableId2 = 2L;
        List<Long> OrderTableIds = Arrays.asList(orderTableId1, orderTableId2);
        List<OrderTable> savedOrderTables = new ArrayList<>();
        given(orderTableDao.findAllByIdIn(OrderTableIds))
                .willReturn(savedOrderTables);

        // when, then
        TableRequestToCreateTableGroup tableRequest1 = new TableRequestToCreateTableGroup(orderTableId1);
        TableRequestToCreateTableGroup tableRequest2 = new TableRequestToCreateTableGroup(orderTableId2);
        TableGroupRequest tableGroupRequest = new TableGroupRequest(Arrays.asList(tableRequest1, tableRequest2));
        assertAll(
                () -> assertThatThrownBy(() -> tableGroupService.create(tableGroupRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findAllByIdIn(OrderTableIds)
        );
    }

    @Test
    void 테이블_그룹을_생성할_때_자리의_상태가_비어있지_않은_주문_테이블이_존재하면_예외가_발생한다() {
        // given
        Long orderTableId1 = 1L;
        Long orderTableId2 = 2L;
        List<Long> orderTableIds = Arrays.asList(orderTableId1, orderTableId2);
        OrderTable orderTable1 = new OrderTable(orderTableId1, null, 0, false);
        OrderTable orderTable2 = new OrderTable(orderTableId2, null, 0, false);
        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);
        given(orderTableDao.findAllByIdIn(orderTableIds))
                .willReturn(orderTables);

        // when, then
        TableRequestToCreateTableGroup tableRequest1 = new TableRequestToCreateTableGroup(orderTableId1);
        TableRequestToCreateTableGroup tableRequest2 = new TableRequestToCreateTableGroup(orderTableId2);
        TableGroupRequest tableGroupRequest = new TableGroupRequest(Arrays.asList(tableRequest1, tableRequest2));
        assertAll(
                () -> assertThatThrownBy(() -> tableGroupService.create(tableGroupRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findAllByIdIn(orderTableIds)
        );
    }

    @Test
    void 테이블_그룹을_생성할_때_이미_테이블_그룹을_가진_주문_테이블이_존재하면_예외가_발생한다() {
        // given
        Long orderTableId1 = 1L;
        Long orderTableId2 = 2L;
        List<Long> orderTableIds = Arrays.asList(orderTableId1, orderTableId2);
        OrderTable orderTable1 = new OrderTable(orderTableId1, 1L, 0, true);
        OrderTable orderTable2 = new OrderTable(orderTableId2, 1L, 0, true);
        given(orderTableDao.findAllByIdIn(orderTableIds))
                .willReturn(Arrays.asList(orderTable1, orderTable2));

        // when, then
        TableRequestToCreateTableGroup tableRequest1 = new TableRequestToCreateTableGroup(orderTableId1);
        TableRequestToCreateTableGroup tableRequest2 = new TableRequestToCreateTableGroup(orderTableId2);
        TableGroupRequest tableGroupRequest = new TableGroupRequest(Arrays.asList(tableRequest1, tableRequest2));
        assertAll(
                () -> assertThatThrownBy(() -> tableGroupService.create(tableGroupRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findAllByIdIn(orderTableIds)
        );
    }

    @Test
    void 테이블_그룹을_해제한다() {
        // given
        long tableGroupIdRequest = 1L;
        List<Long> ids = Collections.singletonList(null);
        OrderTable orderTable = ORDER_TABLE.생성(tableGroupIdRequest, true);
        List<OrderTable> orderTables = Collections.singletonList(orderTable);
        given(orderTableDao.findAllByTableGroupId(tableGroupIdRequest))
                .willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(ids,
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(false);
        given(orderTableDao.save(orderTable))
                .willReturn(null);

        // when
        tableGroupService.ungroup(tableGroupIdRequest);

        // then
        Long tableGroupId = orderTable.getTableGroupId();
        boolean empty = orderTable.isEmpty();
        assertAll(
                () -> assertThat(tableGroupId).isNull(),
                () -> assertThat(empty).isFalse(),
                () -> verify(orderTableDao).findAllByTableGroupId(tableGroupIdRequest),
                () -> verify(orderDao).existsByOrderTableIdInAndOrderStatusIn(ids,
                        Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())),
                () -> verify(orderTableDao).save(orderTable)
        );
    }

    @Test
    void 테이블_그룹을_해제할_때_주문_테이블의_주문_상태가_조리_또는_식사일_경우_예외가_발생한다() {
        // given
        long tableGroupId = 1;
        List<Long> ids = new ArrayList<>();
        given(orderTableDao.findAllByTableGroupId(tableGroupId))
                .willReturn(new ArrayList<>());
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(ids,
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(true);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableGroupService.ungroup(tableGroupId))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findAllByTableGroupId(tableGroupId),
                () -> verify(orderDao).existsByOrderTableIdInAndOrderStatusIn(ids,
                        Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))
        );
    }
}
