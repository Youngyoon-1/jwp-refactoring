package kitchenpos.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OrderTableRepositoryTest {

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private TableGroupRepository tableGroupRepository;

    @Test
    void n_개의_주문_테이블_id_로_주문_테이블을_모두_조회한다() {
        // given
        long orderTableId1 = orderTableRepository.save(new OrderTable())
                .getId();
        long orderTableId2 = orderTableRepository.save(new OrderTable())
                .getId();

        // when
        int orderTableSize = orderTableRepository.findAllByIdIn(Arrays.asList(orderTableId1, orderTableId2))
                .size();

        // then
        Assertions.assertThat(orderTableSize).isEqualTo(2);
    }

    @Test
    void 테이블_그룹_id_로_주문_테이블을_모두_조회한다() {
        // given
        long tableGroupId = tableGroupRepository.save(new TableGroup(null, LocalDateTime.now(), null))
                .getId();
        orderTableRepository.save(new OrderTable(null, tableGroupId, 0, true));
        orderTableRepository.save(new OrderTable(null, tableGroupId, 0, true));

        // when
        int orderTableCount = orderTableRepository.findAllByTableGroupId(tableGroupId)
                .size();

        // then
        Assertions.assertThat(orderTableCount).isEqualTo(2);
    }
}
