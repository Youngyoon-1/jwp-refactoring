package kitchenpos.acceptance;

import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Objects;
import kitchenpos.domain.MenuGroup;
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
        MenuGroup request = MENU_GROUP_1.생성();

        // when
        ResponseEntity<MenuGroup> response = testRestTemplate.postForEntity(
                "/api/menu-groups",
                request,
                MenuGroup.class
        );

        // then
        HttpStatus httpStatus = response.getStatusCode();
        String locationUri = Objects.requireNonNull(response.getHeaders()
                        .getLocation())
                .toString();
        MenuGroup body = response.getBody();
        assert body != null;
        String actualMenuGroupName = request.getName();
        String savedMenuGroupName = body.getName();
        assertAll(
                () -> assertThat(httpStatus).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(locationUri).isEqualTo("/api/menu-groups/" + body.getId()),
                () -> assertThat(actualMenuGroupName).isEqualTo(savedMenuGroupName)
        );
    }

    @Test
    void 메뉴_그룹을_전체_조회한다() {
        // given
        MenuGroup request = MENU_GROUP_1.생성();
        // 메뉴 그룹을 한 개 저장한다
        ResponseEntity<MenuGroup> savedMenuGroup = testRestTemplate.postForEntity(
                "/api/menu-groups",
                request,
                MenuGroup.class
        );

        // when
        ResponseEntity<List<MenuGroup>> response = testRestTemplate.exchange(
                "/api/menu-groups",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MenuGroup>>() {
                }
        );

        // then
        MenuGroup actualMenuGroup = savedMenuGroup.getBody();
        MenuGroup selectedMenuGroup = Objects.requireNonNull(response.getBody())
                .get(0);
        assertThat(actualMenuGroup).isEqualToComparingFieldByField(selectedMenuGroup);
    }
}
