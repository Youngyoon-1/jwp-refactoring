package kitchenpos.acceptance;

import static kitchenpos.support.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@AcceptanceTest
public class TableAcceptanceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void 주문_테이블을_생성한다() {
        // given
        OrderTable request = ORDER_TABLE.생성();

        // when
        ResponseEntity<OrderTable> response = testRestTemplate.postForEntity(
                "/api/tables",
                request,
                OrderTable.class
        );

        // then
        OrderTable actual = request;
        OrderTable expectation = response.getBody();
        HttpStatus expectationHttpStatus = response.getStatusCode();
        assertAll(
                () -> assertThat(HttpStatus.CREATED).isEqualTo(expectationHttpStatus),
                () -> assertThat(actual).isEqualToIgnoringGivenFields(expectation, "id")
        );
    }

    @Test
    void 주문_테이블_한_개_저장하고_주문_테이블_전체를_조회한다() {
        // given
        OrderTable orderTable = ORDER_TABLE.생성();
        ResponseEntity<OrderTable> savedOrderTable = testRestTemplate.postForEntity(
                "/api/tables",
                orderTable,
                OrderTable.class
        );

        // when
        ResponseEntity<List<OrderTable>> response = testRestTemplate.exchange(
                "/api/tables",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OrderTable>>() {
                }
        );

        // then
        OrderTable actual = savedOrderTable.getBody();
        OrderTable expectation = response.getBody()
                .get(0);
        assertThat(actual).isEqualToComparingFieldByField(expectation);
    }

    @Test
    void 주문_테이블_한_개를_저장하고_주문_테이블의_자리_상태를_변경한다() {
        // given
        OrderTable orderTable = ORDER_TABLE.생성();
        ResponseEntity<OrderTable> savedOrderTable = testRestTemplate.postForEntity(
                "/api/tables",
                orderTable,
                OrderTable.class
        );
        OrderTable request = ORDER_TABLE.생성(true);
        long orderTableId = savedOrderTable.getBody()
                .getId();

        // when
        ResponseEntity<OrderTable> response = testRestTemplate.exchange(
                "/api/tables/" + orderTableId + "/empty",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                OrderTable.class
        );

        // then
        OrderTable actual = savedOrderTable.getBody();
        boolean actualEmpty = actual.isEmpty();
        OrderTable expectation = response.getBody();
        boolean expectationEmpty = expectation.isEmpty();
        assertAll(
                () -> assertThat(actual).isEqualToIgnoringGivenFields(expectation, "empty"),
                () -> assertThat(actualEmpty).isNotEqualTo(expectationEmpty)
        );
    }

    @Test
    void 주문_테이블_한_개를_저장하고_저장한_주문_테이블의_손님_수를_수정한다() {
        // given
        OrderTable orderTable = ORDER_TABLE.생성();
        ResponseEntity<OrderTable> savedOrderTable = testRestTemplate.postForEntity(
                "/api/tables",
                orderTable,
                OrderTable.class
        );

        // when
        OrderTable request = ORDER_TABLE.생성(1);
        long orderTableId = savedOrderTable.getBody()
                .getId();
        ResponseEntity<OrderTable> response = testRestTemplate.exchange(
                "/api/tables/" + orderTableId + "/number-of-guests",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                OrderTable.class
        );

        // then
        OrderTable actual = savedOrderTable.getBody();
        int actualNumberOfGuests = request.getNumberOfGuests();
        OrderTable expectation = response.getBody();
        int expectationNumberOfGuests = expectation.getNumberOfGuests();
        assertAll(
                () -> assertThat(actual).isEqualToIgnoringGivenFields(expectation, "numberOfGuests"),
                () -> assertThat(actualNumberOfGuests).isEqualTo(expectationNumberOfGuests)
        );
    }
}
