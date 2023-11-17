package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Objects;
import kitchenpos.dto.request.MenuGroupRequest;
import kitchenpos.dto.response.MenuGroupResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AcceptanceTest
public class MenuGroupAcceptanceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void 메뉴_그룹을_한_개_등록한다() {
        // given
        MenuGroupRequest request = new MenuGroupRequest("메뉴그룹이름");

        // when
        ResponseEntity<MenuGroupResponse> response = testRestTemplate.postForEntity(
                "/api/menu-groups",
                request,
                MenuGroupResponse.class
        );

        // then
        HttpStatus httpStatus = response.getStatusCode();
        String locationUri = Objects.requireNonNull(response.getHeaders()
                        .getLocation())
                .toString();
        MenuGroupResponse menuGroupResponse = response.getBody();
        long menuGroupId = menuGroupResponse.getId();
        String menuGroupName = menuGroupResponse.getName();
        assertAll(
                () -> assertThat(httpStatus).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(locationUri).isEqualTo("/api/menu-groups/" + menuGroupResponse.getId()),
                () -> assertThat(menuGroupId).isNotNull(),
                () -> assertThat(menuGroupName).isEqualTo("메뉴그룹이름")
        );
    }

    @Test
    void 메뉴_그룹을_한_개_저장한_뒤_전체를_조회한다() {
        // given
        MenuGroupRequest request = new MenuGroupRequest("메뉴그룹이름");

        // when
        ResponseEntity<MenuGroupResponse> responseToSave = testRestTemplate.postForEntity(
                "/api/menu-groups",
                request,
                MenuGroupResponse.class
        );

        ResponseEntity<List<MenuGroupResponse>> responseToSelect = testRestTemplate.exchange(
                "/api/menu-groups",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MenuGroupResponse>>() {
                }
        );

        // then
        MenuGroupResponse menuGroupResponseToSave = responseToSave.getBody();
        MenuGroupResponse menuGroupResponseToSelect = responseToSelect.getBody()
                .get(0);
        assertThat(menuGroupResponseToSave).isEqualToComparingFieldByField(menuGroupResponseToSelect);
    }
}
