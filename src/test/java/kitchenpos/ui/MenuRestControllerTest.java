package kitchenpos.ui;

import static kitchenpos.support.fixture.MenuFixture.MENU_1;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(MockitoExtension.class)
class MenuRestControllerTest extends UiTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuRestController menuRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = setupMockMvc(menuRestController);
    }

    @Test
    void 메뉴를_등록한다() throws Exception {
        // given
        Menu menuRequest = MENU_1.생성();
        Menu menuResponse = MENU_1.생성(1L);
        BDDMockito.given(menuService.create(any(Menu.class)))
                .willReturn(menuResponse);

        // when
        String serializedRequestContent = getObjectMapper().writeValueAsString(menuRequest);
        ResultActions resultActions = mockMvc.perform(
                post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequestContent)
        );

        // then
        String serializedResponseContent = getObjectMapper().writeValueAsString(menuResponse);
        assertAll(
                () -> resultActions.andExpect(
                        matchAll(
                                status().isCreated(),
                                header().stringValues("location", "/api/menus/1"),
                                content().string(serializedResponseContent)
                        )
                ),
                () -> verify(menuService).create(any(Menu.class))
        );
    }

    @Test
    void 메뉴_전체를_조회한다() throws Exception {
        // given
        Menu menu = MENU_1.생성();
        List<Menu> menuResponse = Collections.singletonList(menu);
        BDDMockito.given(menuService.list())
                .willReturn(menuResponse);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/menus")
        );

        // then
        String serializedContent = getObjectMapper().writeValueAsString(menuResponse);
        assertAll(
                () -> resultActions.andExpect(
                        matchAll(
                                status().isOk(),
                                content().string(serializedContent)
                        )
                ),
                () -> verify(menuService).list()
        );
    }
}
