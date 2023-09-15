package kitchenpos.application;

import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static kitchenpos.support.fixture.TableGroupFixture.TABLE_GROUP;
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
import java.util.stream.Collectors;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        OrderTable orderTable = ORDER_TABLE.생성(true);
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(orderTable);
        orderTables.add(orderTable);
        TableGroup tableGroupRequest = TABLE_GROUP.생성(orderTables);
        List<Long> idsOfOrderTable = new ArrayList<>();
        idsOfOrderTable.add(null);
        idsOfOrderTable.add(null);
        TableGroup tableGroup = TABLE_GROUP.생성(1L);
        given(orderTableDao.findAllByIdIn(idsOfOrderTable))
                .willReturn(orderTables);
        given(tableGroupDao.save(tableGroupRequest))
                .willReturn(tableGroup);
        given(orderTableDao.save(orderTable))
                .willReturn(null);

        // when
        TableGroup createdTableGroup = tableGroupService.create(tableGroupRequest);

        // then
        LocalDateTime createdDate = tableGroupRequest.getCreatedDate();
        List<OrderTable> createdOrderTables = createdTableGroup.getOrderTables();
        List<Long> createdIdsOfOrderTable = createdOrderTables.stream()
                .map(OrderTable::getTableGroupId)
                .collect(Collectors.toList());
        final List<Boolean> createdEmptyOfOrderTable = createdOrderTables.stream()
                .map(OrderTable::isEmpty)
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(createdDate).isNotNull(),
                () -> assertThat(createdIdsOfOrderTable).containsOnly(1L),
                () -> assertThat(createdEmptyOfOrderTable).containsOnly(false),
                () -> verify(orderTableDao).findAllByIdIn(idsOfOrderTable),
                () -> verify(tableGroupDao).save(tableGroupRequest),
                () -> verify(orderTableDao, times(2)).save(orderTable)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void 테이블_그룹을_생성할_때_주문_테이블이_0개_또는_2개_미만인_경우_예외가_발생한다(final int count) {
        // given
        List<OrderTable> orderTables = new ArrayList<>();
        OrderTable orderTable = ORDER_TABLE.생성();
        for (int i = 0; i < count; i++) {
            orderTables.add(orderTable);
        }
        TableGroup tableGroup = TABLE_GROUP.생성(orderTables);

        // when, then
        assertThatThrownBy(() -> tableGroupService.create(tableGroup))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 테이블_그룹을_생성할_때_등록되지_않은_주문_테이블이_존재하면_예외가_발생한다() {
        // given
        List<OrderTable> orderTablesRequest = new ArrayList<>();
        OrderTable orderTable = ORDER_TABLE.생성();
        orderTablesRequest.add(orderTable);
        orderTablesRequest.add(orderTable);
        TableGroup tableGroupRequest = TABLE_GROUP.생성(orderTablesRequest);
        List<Long> idsOfOrderTableRequest = new ArrayList<>();
        idsOfOrderTableRequest.add(null);
        idsOfOrderTableRequest.add(null);
        List<OrderTable> orderTables = new ArrayList<>();
        given(orderTableDao.findAllByIdIn(idsOfOrderTableRequest))
                .willReturn(orderTables);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableGroupService.create(tableGroupRequest))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findAllByIdIn(idsOfOrderTableRequest)
        );
    }

    @Test
    void 테이블_그룹을_생성할_때_자리의_상태가_차있는_주문_테이블이_존재하면_예외가_발생한다() {
        // given
        List<OrderTable> orderTables = new ArrayList<>();
        OrderTable orderTable = ORDER_TABLE.생성();
        orderTables.add(orderTable);
        orderTables.add(orderTable);
        TableGroup tableGroup = TABLE_GROUP.생성(orderTables);
        List<Long> idsOfOrderTable = new ArrayList<>();
        idsOfOrderTable.add(null);
        idsOfOrderTable.add(null);
        given(orderTableDao.findAllByIdIn(idsOfOrderTable))
                .willReturn(orderTables);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableGroupService.create(tableGroup))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findAllByIdIn(idsOfOrderTable)
        );
    }

    @Test
    void 테이블_그룹을_생성할_때_이미_테이블_그룹을_가진_주문_테이블이_존재하면_예외가_발생한다() {
        // given
        long tableGroupId = 1L;
        OrderTable orderTable = ORDER_TABLE.생성(tableGroupId, true);
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(orderTable);
        orderTables.add(orderTable);
        TableGroup tableGroup = TABLE_GROUP.생성(orderTables);
        List<Long> idsOfOrderTable = new ArrayList<>();
        idsOfOrderTable.add(null);
        idsOfOrderTable.add(null);
        given(orderTableDao.findAllByIdIn(idsOfOrderTable))
                .willReturn(orderTables);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> tableGroupService.create(tableGroup))
                        .isExactlyInstanceOf(IllegalArgumentException.class),
                () -> verify(orderTableDao).findAllByIdIn(idsOfOrderTable)
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
