package kitchenpos.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TableValidatorTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private TableValidator tableValidator;

    @Test
    void 주문_테이블의_자리_상태_변경을_위한_유효성_검증시_테이블_그룹이_있으면_예외가_발생한다() {
        long tableGroupId = 1L;
        OrderTable orderTable = new OrderTable(null, tableGroupId, 0, true);
        Assertions.assertThatThrownBy(
                        () -> tableValidator.validateToChangeEmpty(orderTable)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 테이블의 자리 상태를 변경할 때 주문 테이블이 테이블 그룹에 포함되어 있어서는 안됩니다.");
    }

    @Test
    void 주문_테이블의_자리_상태_변경을_위한_유효성_검증시_주문_테이블의_주문_상태가_조리_또는_식사면_예외가_발생한다() {
        OrderTable orderTable = new OrderTable(1L);
        List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());
        BDDMockito.given(orderRepository.existsByOrderTableIdAndOrderStatusIn(1L, orderStatuses))
                .willReturn(true);

        assertAll(
                () -> Assertions.assertThatThrownBy(
                                () -> tableValidator.validateToChangeEmpty(orderTable)
                        ).isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("주문 테이블의 자리 상태를 변경할 때 주문 테이블의 주문 상태는 조리 또는 식사여서는 안됩니다."),
                () -> BDDMockito.verify(orderRepository)
                        .existsByOrderTableIdAndOrderStatusIn(1L, orderStatuses)
        );
    }

    @Test
    void 주문_테이블의_손님_수를_변경하기_위한_유효성_검증시_주문_테이블이_비어있으면_예외가_발생한다() {
        OrderTable orderTable = new OrderTable(null, null, 0, true);

        Assertions.assertThatThrownBy(
                        () -> tableValidator.validateToChangeNumberOfGuests(orderTable)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 테이블의 손님 수를 변경할 때 주문 테이블의 자리가 비어있어서는 안됩니다.");
    }
}
