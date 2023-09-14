package kitchenpos.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import kitchenpos.dao.MenuGroupDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        given(menuGroupDao.save(any()))
                .willReturn(null);

        // when
        menuGroupService.create(null);

        // then
        verify(menuGroupDao).save(null);
    }

    @Test
    void 메뉴_그룹_전체를_조회한다() {
        // given
        given(menuGroupDao.findAll())
                .willReturn(null);

        // when
        menuGroupService.list();

        // then
        verify(menuGroupDao).findAll();
    }
}
