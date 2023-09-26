package kitchenpos.application;

import static kitchenpos.support.fixture.MenuGroupFixture.MENU_GROUP_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.domain.MenuGroup;
import kitchenpos.dto.request.MenuGroupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    void 메뉴_그룹을_생성한다() {
        // given
        MenuGroupRequest menuGroupRequest = new MenuGroupRequest("메뉴그룹1");
        MenuGroup willReturnValue = MENU_GROUP_1.생성(1L);
        given(menuGroupDao.save(any(MenuGroup.class)))
                .willReturn(willReturnValue);

        // when
        menuGroupService.create(menuGroupRequest);

        // then
        verify(menuGroupDao).save(ArgumentMatchers.any(MenuGroup.class));
    }

    @Test
    void 메뉴_그룹_전체를_조회한다() {
        // given
        MenuGroup menuGroup = MENU_GROUP_1.생성(1L);
        List<MenuGroup> menuGroups = Collections.singletonList(menuGroup);
        given(menuGroupDao.findAll())
                .willReturn(menuGroups);

        // when
        menuGroupService.list();

        // then
        verify(menuGroupDao).findAll();
    }
}
