package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTables;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.request.TableGroupRequest;
import kitchenpos.dto.response.TableGroupResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TableGroupService {

    private final OrderDao orderDao;
    private final OrderTableDao orderTableDao;
    private final TableGroupDao tableGroupDao;

    public TableGroupService(final OrderDao orderDao, final OrderTableDao orderTableDao,
                             final TableGroupDao tableGroupDao) {
        this.orderDao = orderDao;
        this.orderTableDao = orderTableDao;
        this.tableGroupDao = tableGroupDao;
    }

    @Transactional
    public TableGroupResponse create(final TableGroupRequest tableGroupRequest) {
        final TableGroup tableGroup = tableGroupRequest.toEntity();
        final List<Long> orderTableIds = tableGroup.collectOrderTableIds();
        final List<OrderTable> savedOrderTables = orderTableDao.findAllByIdIn(orderTableIds);
        tableGroup.validateToSave(savedOrderTables);
        OrderTables orderTables = OrderTables.createToSaveTableGroup(savedOrderTables);
        final TableGroup savedTableGroup = tableGroupDao.save(tableGroup);
        final List<OrderTable> orderTablesToSave = orderTables.getOrderTables();
        for (final OrderTable orderTableToSave : orderTablesToSave) {
            orderTableToSave.updateTableGroupId(savedTableGroup);
            orderTableDao.save(orderTableToSave);
        }
        savedTableGroup.setOrderTables(orderTablesToSave);

        return new TableGroupResponse(savedTableGroup);
    }

    @Transactional
    public void ungroup(final Long tableGroupId) {
        final List<OrderTable> savedOrderTables = orderTableDao.findAllByTableGroupId(tableGroupId);
        final OrderTables orderTables = OrderTables.createToUngroup(savedOrderTables);
        final List<Long> orderTableIds = orderTables.collectIds();
        if (orderDao.existsByOrderTableIdInAndOrderStatusIn(
                orderTableIds, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException();
        }
        for (final OrderTable orderTable : orderTables.getOrderTables()) {
            orderTableDao.save(orderTable);
        }
    }
}
