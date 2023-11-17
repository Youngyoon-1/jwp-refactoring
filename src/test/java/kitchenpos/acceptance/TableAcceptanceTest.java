package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.dto.request.TableRequestToChangeEmpty;
import kitchenpos.dto.request.TableRequestToChangeNumberOfGuests;
import kitchenpos.dto.request.TableRequestToCreate;
import kitchenpos.dto.response.OrderTableResponse;
import org.assertj.core.api.Assertions;
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
        TableRequestToCreate request = new TableRequestToCreate(1, false);

        // when
        ResponseEntity<OrderTableResponse> response = testRestTemplate.postForEntity(
                "/api/tables",
                request,
                OrderTableResponse.class
        );

        // then
        HttpStatus httpStatus = response.getStatusCode();
        OrderTableResponse orderTableResponse = response.getBody();
        long orderTableId = orderTableResponse.getId();
        Long tableGroupId = orderTableResponse.getTableGroupId();
        int numberOfGuests = orderTableResponse.getNumberOfGuests();
        boolean empty = orderTableResponse.isEmpty();
        assertAll(
                () -> assertThat(httpStatus).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(orderTableId).isNotNull(),
                () -> assertThat(tableGroupId).isNull(),
                () -> assertThat(numberOfGuests).isOne(),
                () -> assertThat(empty).isFalse()
        );
    }

    @Test
    void 주문_테이블_한_개_저장하고_주문_테이블_전체를_조회한다() {
        // given
        TableRequestToCreate request = new TableRequestToCreate(1, false);

        // when
        ResponseEntity<OrderTableResponse> responseToSave = testRestTemplate.postForEntity(
                "/api/tables",
                request,
                OrderTableResponse.class
        );
        ResponseEntity<List<OrderTableResponse>> responseToSelect = testRestTemplate.exchange(
                "/api/tables",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OrderTableResponse>>() {
                }
        );

        // then
        OrderTableResponse orderTableResponseToSave = responseToSave.getBody();
        OrderTableResponse orderTableResponseToSelect = responseToSelect.getBody()
                .get(0);
        assertThat(orderTableResponseToSave).isEqualToComparingFieldByField(orderTableResponseToSelect);
    }

    @Test
    void 주문_테이블_한_개를_저장하고_주문_테이블의_자리_상태를_변경한다() {
        // given
        TableRequestToCreate requestToSave = new TableRequestToCreate(1, false);

        // when
        ResponseEntity<OrderTableResponse> responseToSave = testRestTemplate.postForEntity(
                "/api/tables",
                requestToSave,
                OrderTableResponse.class
        );
        OrderTableResponse orderTableResponseToSave = responseToSave.getBody();
        long orderTableId = orderTableResponseToSave.getId();
        TableRequestToChangeEmpty requestToChange = new TableRequestToChangeEmpty(true);
        ResponseEntity<OrderTableResponse> responseToChange = testRestTemplate.exchange(
                "/api/tables/" + orderTableId + "/empty",
                HttpMethod.PUT,
                new HttpEntity<>(requestToChange),
                OrderTableResponse.class
        );

        // then
        OrderTableResponse orderTableResponseToChange = responseToChange.getBody();
        boolean empty = orderTableResponseToChange.isEmpty();
        assertAll(
                () -> Assertions.assertThat(orderTableResponseToSave)
                        .isEqualToIgnoringGivenFields(orderTableResponseToChange, "empty"),
                () -> Assertions.assertThat(empty).isTrue()
        );
    }

    @Test
    void 주문_테이블_한_개를_저장하고_저장한_주문_테이블의_손님_수를_수정한다() {
        // given
        TableRequestToCreate requestToSave = new TableRequestToCreate(1, false);

        // when
        ResponseEntity<OrderTableResponse> responseToSave = testRestTemplate.postForEntity(
                "/api/tables",
                requestToSave,
                OrderTableResponse.class
        );
        TableRequestToChangeNumberOfGuests requestToChangeNumberOfGuests = new TableRequestToChangeNumberOfGuests(
                2);
        OrderTableResponse orderTableResponseToSave = responseToSave.getBody();
        long orderTableId = orderTableResponseToSave.getId();
        ResponseEntity<OrderTableResponse> responseToChange = testRestTemplate.exchange(
                "/api/tables/" + orderTableId + "/number-of-guests",
                HttpMethod.PUT,
                new HttpEntity<>(requestToChangeNumberOfGuests),
                OrderTableResponse.class
        );

        // then
        OrderTableResponse orderTableResponseToChange = responseToChange.getBody();
        int numberOfGuests = orderTableResponseToChange.getNumberOfGuests();
        assertAll(
                () -> Assertions.assertThat(orderTableResponseToSave)
                        .isEqualToIgnoringGivenFields(orderTableResponseToChange, "numberOfGuests"),
                () -> Assertions.assertThat(numberOfGuests).isEqualTo(2)
        );
    }
}
