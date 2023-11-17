package kitchenpos.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.dto.request.MenuRequest;
import kitchenpos.dto.response.MenuResponse;
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
        List<MenuProduct> menuProducts = new ArrayList<>();
        Menu menu = new Menu(1L, "메뉴", BigDecimal.TEN, 1L, menuProducts);
        MenuResponse menuResponse = new MenuResponse(menu);
        BDDMockito.given(menuService.create(any(MenuRequest.class)))
                .willReturn(menuResponse);

        // when
        MenuRequest menuRequest = new MenuRequest("메뉴", BigDecimal.TEN, 1L, menuProducts);
        String serializedRequest = getObjectMapper().writeValueAsString(menuRequest);
        ResultActions resultActions = mockMvc.perform(
                post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequest)
        );

        // then
        String serializedResponse = getObjectMapper().writeValueAsString(menuResponse);
        assertAll(
                () -> resultActions.andExpect(
                        matchAll(
                                status().isCreated(),
                                header().stringValues("location", "/api/menus/1"),
                                content().string(serializedResponse)
                        )
                ),
                () -> verify(menuService).create(any(MenuRequest.class))
        );
    }

    @Test
    void 메뉴_전체를_조회한다() throws Exception {
        // given
        Menu menu = new Menu(1L, "메뉴", BigDecimal.ZERO, 1L, new ArrayList<>());
        List<MenuResponse> menuResponses = Collections.singletonList(new MenuResponse(menu));
        BDDMockito.given(menuService.list())
                .willReturn(menuResponses);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/menus")
        );

        // then
        String serializedContent = getObjectMapper().writeValueAsString(menuResponses);
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
