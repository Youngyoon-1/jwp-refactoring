package kitchenpos.domain;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class OrderValidator {

    private final MenuRepository menuRepository;
    private final OrderTableRepository orderTableDao;

    public OrderValidator(final MenuRepository menuRepository, final OrderTableRepository orderTableDao) {
        this.menuRepository = menuRepository;
        this.orderTableDao = orderTableDao;
    }

    public void validateToCreateOrder(final Order order) {
        validateOrderLineItem(order);
        validateOrderTable(order);
    }

    private void validateOrderLineItem(final Order order) {
        final List<OrderLineItem> orderLineItems = order.getOrderLineItems();
        if (CollectionUtils.isEmpty(orderLineItems)) {
            throw new IllegalArgumentException("주문 항목이 존재하지 않습니다.");
        }

        final List<Long> menuIds = order.collectMenuIds();
        final long menuCount = menuRepository.countByIdIn(menuIds);
        if (orderLineItems.size() != menuCount) {
            throw new IllegalArgumentException("주문 항목에 저장되지 않은 메뉴가 존재합니다.");
        }
    }

    private void validateOrderTable(final Order order) {
        final OrderTable orderTable = orderTableDao.findById(order.getOrderTableId())
                .orElseThrow(
                        () -> new IllegalArgumentException("저장되지 않은 주문 테이블입니다.")
                );
        if (orderTable.isEmpty()) {
            throw new IllegalArgumentException("주문 테이블이 비어있습니다.");
        }
    }

    public void validateToChangeOrderStatus(final Order order) {
        if (Objects.equals(OrderStatus.COMPLETION.name(), order.getOrderStatus())) {
            throw new IllegalArgumentException("주문의 상태가 완료인 경우 주문 상태를 변경할 수 없습니다.");
        }
    }
}
