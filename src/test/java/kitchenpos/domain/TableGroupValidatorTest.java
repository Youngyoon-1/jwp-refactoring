package kitchenpos.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TableGroupValidatorTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private TableGroupValidator tableGroupValidator;

    @Test
    void 테이블_그룹을_생성하기_위한_유효성_검증시_주문_테이블이_null_인_경우_예외가_발생한다() {
        TableGroup tableGroup = new TableGroup(null);

        Assertions.assertThatThrownBy(
                        () -> tableGroupValidator.validateToCreate(tableGroup)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("테이블 그룹 생성시 주문 테이블 개수는 2개 이상이어야 합니다.");
    }

    @Test
    void 테이블_그룹을_생성하기_위한_유효성_검증시_주문_테이블_개수가_2_미만이면_예외가_발생한다() {
        OrderTable orderTable = new OrderTable(null);
        TableGroup tableGroup = new TableGroup(Collections.singletonList(orderTable));

        Assertions.assertThatThrownBy(
                        () -> tableGroupValidator.validateToCreate(tableGroup)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("테이블 그룹 생성시 주문 테이블 개수는 2개 이상이어야 합니다.");
    }

    @Test
    void 테이블_그룹을_생성하기_위한_유효성_검증시_저장되지_않은_주문_테이블이_존재할_경우_예외가_발생한다() {
        OrderTable savedOrderTable = new OrderTable();
        List<OrderTable> savedOrderTables = Collections.singletonList(savedOrderTable);

        Assertions.assertThatThrownBy(
                        () -> tableGroupValidator.validateToCreate(new ArrayList<>(), savedOrderTables)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("테이블 그룹 생성시 주문 테이블이 저장되어 있어야 합니다.");
    }

    @Test
    void 테이블_그룹을_생성하기_위한_유효성_검증시_주문_테이블이_비어있지_않으면_예외가_발생한다() {
        OrderTable orderTable = new OrderTable(null, null, 1, false);
        List<OrderTable> orderTables = Collections.singletonList(orderTable);

        Assertions.assertThatThrownBy(
                        () -> tableGroupValidator.validateToCreate(orderTables, orderTables)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("테이블 그룹 생성시 주문 테이블이 비어있어야 합니다.");
    }

    @Test
    void 테이블_그룹을_생성하기_위한_유효성_검증시_주문_테이블의_테이블_그룹이_이미_존재하면_예외가_발생한다() {
        OrderTable orderTable = new OrderTable(null, 1L, 1, true);
        List<OrderTable> orderTables = Collections.singletonList(orderTable);

        Assertions.assertThatThrownBy(
                        () -> tableGroupValidator.validateToCreate(orderTables, orderTables)
                ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("테이블 그룹 생성시 주문 테이블의 테이블 그룹이 이미 존재해서는 안됩니다.");
    }

    @Test
    void 테이블_그룹_해제를_위한_유효성_검증시_주문_테이블의_주문_상태가_조리_또는_식사인_겅우_예외가_발생한다() {
        long orderTableId = 1L;
        OrderTable orderTable = new OrderTable(orderTableId);
        List<Long> orderTableIds = Collections.singletonList(orderTableId);
        List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());
        BDDMockito.given(orderRepository.existsByOrderTableIdInAndOrderStatusIn(orderTableIds, orderStatuses))
                .willReturn(true);

        assertAll(
                () -> Assertions.assertThatThrownBy(
                                () -> tableGroupValidator.validateToUngroup(Collections.singletonList(orderTable))
                        ).isExactlyInstanceOf(IllegalArgumentException.class)
                        .hasMessage("테이블 그룹 해제시 주문 테이블의 주문 상태가 식사 또는 조리면 안됩니다."),
                () -> BDDMockito.verify(orderRepository)
                        .existsByOrderTableIdInAndOrderStatusIn(orderTableIds, orderStatuses)
        );
    }
}
