package kitchenpos.application;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.TableValidator;
import kitchenpos.dto.request.TableRequestToChangeEmpty;
import kitchenpos.dto.request.TableRequestToChangeNumberOfGuests;
import kitchenpos.dto.request.TableRequestToCreate;
import kitchenpos.dto.response.OrderTableResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TableService {

    private final TableValidator tableValidator;
    private final OrderTableRepository orderTableDao;

    public TableService(final TableValidator tableValidator, final OrderTableRepository orderTableDao) {
        this.tableValidator = tableValidator;
        this.orderTableDao = orderTableDao;
    }

    @Transactional
    public OrderTableResponse create(final TableRequestToCreate tableRequest) {
        final OrderTable orderTable = tableRequest.toEntity();
        final OrderTable savedOrderTable = orderTableDao.save(orderTable);
        return new OrderTableResponse(savedOrderTable);
    }

    public List<OrderTableResponse> list() {
        final List<OrderTable> orderTables = orderTableDao.findAll();
        return orderTables.stream()
                .map(OrderTableResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderTableResponse changeEmpty(final Long orderTableId, final TableRequestToChangeEmpty tableRequest) {
        final OrderTable orderTable = orderTableDao.findById(orderTableId)
                .orElseThrow(() -> new IllegalArgumentException("주문 테이블의 자리 상태를 변경할 때 주문 테이블이 저장되어 있어야 합니다."));
        tableValidator.validateToChangeEmpty(orderTable);
        orderTable.updateEmpty(tableRequest.isEmpty());
        return new OrderTableResponse(orderTable);
    }

    @Transactional
    public OrderTableResponse changeNumberOfGuests(final Long orderTableId,
                                                   final TableRequestToChangeNumberOfGuests tableRequest) {
        if (tableRequest.getNumberOfGuests() < 0) {
            throw new IllegalArgumentException("주문 테이블의 손님 수를 변경할 때 변경할 수가 음수면 안됩니다.");
        }
        final OrderTable orderTable = orderTableDao.findById(orderTableId)
                .orElseThrow(() -> new IllegalArgumentException("주문 테이블의 손님 수를 변경할 때 주문 테이블이 저장되어 있어야 합니다."));
        tableValidator.validateToChangeNumberOfGuests(orderTable);
        orderTable.updateNumberOfGuests(tableRequest.toEntity());
        return new OrderTableResponse(orderTable);
    }
}
