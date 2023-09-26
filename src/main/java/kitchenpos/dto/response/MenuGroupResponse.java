package kitchenpos.dto.response;

import kitchenpos.domain.MenuGroup;

public class MenuGroupResponse {

    private long id;
    private String name;

    private MenuGroupResponse() {
    }

    public MenuGroupResponse(final MenuGroup menuGroup) {
        this.id = menuGroup.getId();
        this.name = menuGroup.getName();
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }
}
