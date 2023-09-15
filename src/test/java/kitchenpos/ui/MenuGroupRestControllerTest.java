package kitchenpos.ui;

import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
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
        MenuGroup menuGroupRequest = MENU_GROUP_1.생성();
        MenuGroup menuGroupResponse = MENU_GROUP_1.생성(1L);
        given(menuGroupService.create(any(MenuGroup.class)))
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
                () -> verify(menuGroupService).create(any(MenuGroup.class))
        );
    }

    @Test
    void 메뉴_그룹_전체를_조회한다() throws Exception {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성();
        List<MenuGroup> menuGroups = Collections.singletonList(menuGroup);
        given(menuGroupService.list())
                .willReturn(menuGroups);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        String serializedContent = getObjectMapper().writeValueAsString(menuGroups);
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
