package kitchenpos.application;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuProductRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.MenuValidator;
import kitchenpos.dto.request.MenuRequest;
import kitchenpos.dto.response.MenuResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuProductRepository menuProductRepository;
    private final MenuValidator menuValidator;

    public MenuService(
            final MenuRepository menuRepository,
            final MenuProductRepository menuProductRepository,
            final MenuValidator menuValidator
    ) {
        this.menuRepository = menuRepository;
        this.menuProductRepository = menuProductRepository;
        this.menuValidator = menuValidator;
    }

    @Transactional
    public MenuResponse create(final MenuRequest menuRequest) {
        final Menu menu = menuRequest.toEntity();
        menuValidator.validate(menu);
        final Menu savedMenu = menuRepository.save(menu);
        final long menuId = savedMenu.getId();
        for (final MenuProduct menuProduct : savedMenu.getMenuProducts()) {
            menuProduct.updateMenuId(menuId);
            menuProductRepository.save(menuProduct);
        }
        return new MenuResponse(savedMenu);
    }

    public List<MenuResponse> list() {
        final List<Menu> menus = menuRepository.findAll();
        return menus.stream()
                .map(MenuResponse::new)
                .collect(Collectors.toList());
    }
}
