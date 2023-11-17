package kitchenpos.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderValidatorTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableDao;

    @InjectMocks
    private OrderValidator orderValidator;

    @Test
    void 주문_유효성_검증을_한다() {
        long orderTableId = 1L;
        OrderLineItem orderLineItem = new OrderLineItem();
        Order order = new Order(
                null,
                orderTableId,
                null,
                null,
                Collections.singletonList(orderLineItem)
        );
        BDDMockito.given(menuRepository.countByIdIn(ArgumentMatchers.anyList()))
                .willReturn(1L);
        OrderTable orderTable = new OrderTable();
        BDDMockito.given(orderTableDao.findById(orderTableId))
                .willReturn(Optional.of(orderTable));

        assertAll(
                () -> Assertions.assertThatCode(
                        () -> orderValidator.validateToCreateOrder(order)
                ).doesNotThrowAnyException(),
                () -> BDDMockito.verify(menuRepository).countByIdIn(ArgumentMatchers.anyList()),
                () -> BDDMockito.verify(orderTableDao).findById(orderTableId)
        );
    }

    @Test
    void 주문_유효성_검증시_주문_항목이_null_이면_예외가_발생한다() {
        Order order = new Order();

        Assertions.assertThatThrownBy(
                        () -> orderValidator.validateToCreateOrder(order)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 항목이 존재하지 않습니다.");
    }

    @Test
    void 주문_유효성_검증시_주문_항목이_비어있으면_예외가_발생한다() {
        Order order = new Order(null, null, null, null, new ArrayList<>());

        Assertions.assertThatThrownBy(
                        () -> orderValidator.validateToCreateOrder(order)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 항목이 존재하지 않습니다.");
    }

    @Test
    void 주문_유효성_검증시_주문_항목의_메뉴가_저장되어_있지_않은_경우_예외가_발생한다() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Order order = new Order(null,
                null,
                null,
                null,
                Collections.singletonList(orderLineItem)
        );
        BDDMockito.given(menuRepository.countByIdIn(ArgumentMatchers.anyList()))
                .willReturn(0L);

        assertAll(
                () -> Assertions.assertThatThrownBy(
                                () -> orderValidator.validateToCreateOrder(order)
                        ).isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("주문 항목에 저장되지 않은 메뉴가 존재합니다."),
                () -> BDDMockito.verify(menuRepository)
                        .countByIdIn(ArgumentMatchers.anyList())
        );
    }

    @Test
    void 주문_유효성_검증시_주문_테이블이_저장되어_있지_않으면_예외가_발생한다() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Long orderTableId = 1L;
        Order order = new Order(
                null,
                orderTableId,
                null,
                null,
                Collections.singletonList(orderLineItem)
        );
        BDDMockito.given(menuRepository.countByIdIn(ArgumentMatchers.anyList()))
                .willReturn(1L);
        BDDMockito.given(orderTableDao.findById(orderTableId))
                .willReturn(Optional.empty());

        assertAll(
                () -> Assertions.assertThatThrownBy(
                                () -> orderValidator.validateToCreateOrder(order)
                        ).isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("저장되지 않은 주문 테이블입니다."),
                () -> BDDMockito.verify(menuRepository).countByIdIn(ArgumentMatchers.anyList()),
                () -> BDDMockito.verify(orderTableDao).findById(orderTableId)
        );
    }

    @Test
    void 주문_유효성_검증시_주문_테이블이_비어있으면_예외가_발생한다() {
        Long orderTableId = 1L;
        OrderLineItem orderLineItem = new OrderLineItem();
        Order order = new Order(
                null,
                orderTableId,
                null,
                null,
                Collections.singletonList(orderLineItem)
        );
        BDDMockito.given(menuRepository.countByIdIn(ArgumentMatchers.anyList()))
                .willReturn(1L);
        OrderTable orderTable = new OrderTable(
                null,
                null,
                1,
                true
        );
        BDDMockito.given(orderTableDao.findById(orderTableId))
                .willReturn(Optional.of(orderTable));

        assertAll(
                () -> Assertions.assertThatThrownBy(
                                () -> orderValidator.validateToCreateOrder(order)
                        ).isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("주문 테이블이 비어있습니다."),
                () -> BDDMockito.verify(menuRepository)
                        .countByIdIn(ArgumentMatchers.anyList()),
                () -> BDDMockito.verify(orderTableDao)
                        .findById(orderTableId)
        );
    }

    @Test
    void 주문_상태_변경에_대한_유효성_검증시_주문의_상태가_완료인_경우_예외가_발생한다() {
        Order order = new Order(
                null,
                null,
                OrderStatus.COMPLETION.name(),
                null,
                null
        );

        Assertions.assertThatThrownBy(
                        () -> orderValidator.validateToChangeOrderStatus(order)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문의 상태가 완료인 경우 주문 상태를 변경할 수 없습니다.");
    }
}
