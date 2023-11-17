package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.dto.request.MenuGroupRequest;
import kitchenpos.dto.response.MenuGroupResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    void 메뉴_그룹을_생성한다() {
        // given
        MenuGroup savedMeuGroup = new MenuGroup(1L, "메뉴그룹이름");
        given(menuGroupRepository.save(any(MenuGroup.class)))
                .willReturn(savedMeuGroup);

        // when
        MenuGroupRequest menuGroupRequest = new MenuGroupRequest("메뉴그룹이름");
        MenuGroupResponse menuGroupResponse = menuGroupService.create(menuGroupRequest);

        // then
        long actualMenuGroupId = menuGroupResponse.getId();
        String actualMenuGroupName = menuGroupResponse.getName();
        assertAll(
                () -> BDDMockito.verify(menuGroupRepository).save(ArgumentMatchers.any(MenuGroup.class)),
                () -> assertThat(actualMenuGroupId).isEqualTo(1L),
                () -> assertThat(actualMenuGroupName).isEqualTo("메뉴그룹이름")
        );
    }

    @Test
    void 메뉴_그룹_전체를_조회한다() {
        // given
        MenuGroup menuGroup = new MenuGroup(1L, "메뉴그룹이름");
        List<MenuGroup> menuGroups = Collections.singletonList(menuGroup);
        given(menuGroupRepository.findAll())
                .willReturn(menuGroups);

        // when
        List<MenuGroupResponse> menuGroupResponses = menuGroupService.list();

        // then
        MenuGroupResponse menuGroupResponse = menuGroupResponses.get(0);
        long actualMenuGroupId = menuGroupResponse.getId();
        String actualMenuGroupName = menuGroupResponse.getName();
        assertAll(
                () -> BDDMockito.verify(menuGroupRepository).findAll(),
                () -> assertThat(menuGroupResponses.size()).isOne(),
                () -> assertThat(actualMenuGroupId).isEqualTo(1L),
                () -> assertThat(actualMenuGroupName).isEqualTo("메뉴그룹이름")
        );
    }
}
