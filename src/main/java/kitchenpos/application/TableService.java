package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.request.TableRequestToChangeEmpty;
import kitchenpos.dto.request.TableRequestToChangeNumberOfGuests;
import kitchenpos.dto.request.TableRequestToCreate;
import kitchenpos.dto.response.OrderTableResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TableService {

    private final OrderDao orderDao;
    private final OrderTableDao orderTableDao;

    public TableService(final OrderDao orderDao, final OrderTableDao orderTableDao) {
        this.orderDao = orderDao;
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
                .orElseThrow(IllegalArgumentException::new);

        orderTable.validateToUpdateEmpty();

        if (orderDao.existsByOrderTableIdAndOrderStatusIn(
                orderTableId, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException();
        }

        orderTable.setEmpty(tableRequest.isEmpty());
        final OrderTable savedOrderTable = orderTableDao.save(orderTable);
        return new OrderTableResponse(savedOrderTable);
    }

    @Transactional
    public OrderTableResponse changeNumberOfGuests(final Long orderTableId,
                                                   final TableRequestToChangeNumberOfGuests tableRequest) {
        final OrderTable orderTable = tableRequest.toEntity();

        final OrderTable selectedOrderTable = orderTableDao.findById(orderTableId)
                .orElseThrow(IllegalArgumentException::new);

        selectedOrderTable.updateNumberOfGuests(orderTable);

        final OrderTable savedOrderTable = orderTableDao.save(selectedOrderTable);
        return new OrderTableResponse(savedOrderTable);
    }
}
