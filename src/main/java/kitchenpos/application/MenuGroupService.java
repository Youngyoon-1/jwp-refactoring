package kitchenpos.application;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.dto.request.MenuGroupRequest;
import kitchenpos.dto.response.MenuGroupResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;

    public MenuGroupService(final MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    @Transactional
    public MenuGroupResponse create(final MenuGroupRequest menuGroupRequest) {
        final MenuGroup menuGroup = menuGroupRequest.toEntity();
        final MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);
        return new MenuGroupResponse(savedMenuGroup);
    }

    public List<MenuGroupResponse> list() {
        final List<MenuGroup> menuGroups = menuGroupRepository.findAll();
        return menuGroups.stream()
                .map(MenuGroupResponse::new)
                .collect(Collectors.toList());
    }
}
