package kitchenpos.ui;

import static kitchenpos.support.fixture.TableGroupFixture.TABLE_GROUP;
import static org.junit.jupiter.api.Assertions.assertAll;

import kitchenpos.application.TableGroupService;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(MockitoExtension.class)
class TableGroupRestControllerTest extends UiTest {

    @Mock
    private TableGroupService tableGroupService;

    @InjectMocks
    private TableGroupRestController tableGroupRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = setupMockMvc(tableGroupRestController);
    }

    @Test
    void 테이블_그룹을_생성한다() throws Exception {
        // given
        TableGroup tableGroupRequest = TABLE_GROUP.생성();
        TableGroup tableGroupResponse = TABLE_GROUP.생성(1L);
        BDDMockito.given(tableGroupService.create(ArgumentMatchers.any(TableGroup.class)))
                .willReturn(tableGroupResponse);

        // when
        String serializedRequestContent = getObjectMapper().writeValueAsString(tableGroupRequest);
        System.out.println(serializedRequestContent);
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/table-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequestContent)
        );

        // then
        String serializedResponseContent = getObjectMapper().writeValueAsString(tableGroupResponse);
        assertAll(
                () -> resultActions.andExpect(
                        ResultMatcher.matchAll(
                                MockMvcResultMatchers.status().isCreated(),
                                MockMvcResultMatchers.header().string("location", "/api/table-groups/1"),
                                MockMvcResultMatchers.content().string(serializedResponseContent)
                        )
                ),
                () -> BDDMockito.verify(tableGroupService).create(ArgumentMatchers.any(TableGroup.class))
        );
    }

    @Test
    void 테이블_그룹을_삭제한다() throws Exception {
        // given
        long request = 1L;
        BDDMockito.willDoNothing()
                .given(tableGroupService)
                .ungroup(request);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/table-groups/1")
        );

        // then
        assertAll(
                () -> resultActions.andExpect(
                        MockMvcResultMatchers.status().isNoContent()
                ),
                () -> BDDMockito.verify(tableGroupService).ungroup(request)
        );
    }
}
