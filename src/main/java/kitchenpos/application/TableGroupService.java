package kitchenpos.application;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.TableGroup;
import kitchenpos.domain.TableGroupRepository;
import kitchenpos.domain.TableGroupValidator;
import kitchenpos.dto.request.TableGroupRequest;
import kitchenpos.dto.response.TableGroupResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TableGroupService {

    private final OrderTableRepository orderTableRepository;
    private final TableGroupRepository tableGroupRepository;
    private final TableGroupValidator tableGroupValidator;

    public TableGroupService(final OrderTableRepository orderTableRepository,
                             final TableGroupRepository tableGroupRepository,
                             final TableGroupValidator tableGroupValidator) {
        this.orderTableRepository = orderTableRepository;
        this.tableGroupRepository = tableGroupRepository;
        this.tableGroupValidator = tableGroupValidator;
    }

    @Transactional
    public TableGroupResponse create(final TableGroupRequest tableGroupRequest) {
        final TableGroup tableGroup = tableGroupRequest.toEntity();
        tableGroupValidator.validateToCreate(tableGroup);
        final List<OrderTable> orderTables = orderTableRepository.findAllByIdIn(tableGroup.collectOrderTableIds());
        tableGroupValidator.validateToCreate(tableGroup.getOrderTables(), orderTables);
        tableGroup.updateCreatedDate(LocalDateTime.now());
        final TableGroup savedTableGroup = tableGroupRepository.save(tableGroup);
        final long tableGroupId = savedTableGroup.getId();
        for (final OrderTable orderTable : orderTables) {
            orderTable.updateTableGroupId(tableGroupId);
        }
        savedTableGroup.setOrderTables(orderTables);
        return new TableGroupResponse(savedTableGroup);
    }

    @Transactional
    public void ungroup(final Long tableGroupId) {
        final List<OrderTable> orderTables = orderTableRepository.findAllByTableGroupId(tableGroupId);
        tableGroupValidator.validateToUngroup(orderTables);
        for (final OrderTable orderTable : orderTables) {
            orderTable.ungroup();
        }
    }
}
