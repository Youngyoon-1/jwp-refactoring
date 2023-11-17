package kitchenpos.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.dto.request.MenuGroupRequest;
import kitchenpos.dto.response.MenuGroupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(MockitoExtension.class)
class MenuGroupRestControllerTest extends UiTest {
    // 컨트롤러 테스트 리펙토링
    @Mock
    private MenuGroupService menuGroupService;

    @InjectMocks
    private MenuGroupRestController menuGroupRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = setupMockMvc(menuGroupRestController);
    }

    @Test
    void 메뉴_그룹을_생성한다() throws Exception {
        // given
        MenuGroupRequest menuGroupRequest = new MenuGroupRequest("메뉴그룹1");
        MenuGroup menuGroup = new MenuGroup(1L, "메뉴그룹1");
        MenuGroupResponse menuGroupResponse = new MenuGroupResponse(menuGroup);
        given(menuGroupService.create(any(MenuGroupRequest.class)))
                .willReturn(menuGroupResponse);

        // when
        String serializedRequestContent = getObjectMapper().writeValueAsString(menuGroupRequest);
        ResultActions resultActions = mockMvc.perform(
                post("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequestContent)
        );

        // then
        String serializedResponseContent = getObjectMapper().writeValueAsString(menuGroupResponse);
        assertAll(
                () -> resultActions.andExpect(
                        matchAll(
                                status().isCreated(),
                                header().stringValues("location", "/api/menu-groups/1"),
                                content().string(serializedResponseContent)
                        )
                ),
                () -> verify(menuGroupService).create(any(MenuGroupRequest.class))
        );
    }

    @Test
    void 메뉴_그룹_전체를_조회한다() throws Exception {
        // given
        MenuGroup menuGroup = new MenuGroup(1L, "메뉴그룹");
        List<MenuGroupResponse> menuGroupResponses = Collections.singletonList(new MenuGroupResponse(menuGroup));
        given(menuGroupService.list())
                .willReturn(menuGroupResponses);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        String serializedContent = getObjectMapper().writeValueAsString(menuGroupResponses);
        assertAll(
                () -> resultActions.andExpect(
                        matchAll(
                                status().isOk(),
                                content().string(serializedContent)
                        )
                ),
                () -> verify(menuGroupService).list()
        );
    }
}
