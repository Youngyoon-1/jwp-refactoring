package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MenuValidator {

    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productDao;

    public MenuValidator(final MenuGroupRepository menuGroupRepository, final ProductRepository productDao) {
        this.menuGroupRepository = menuGroupRepository;
        this.productDao = productDao;
    }

    public void validate(final Menu menu) {
        validatePrice(menu);
        validateMenuGroup(menu);
    }

    private void validatePrice(final Menu menu) {
        validatePrice(menu.getPrice());
        final List<MenuProduct> menuProducts = menu.getMenuProducts();
        BigDecimal sum = BigDecimal.ZERO;
        for (final MenuProduct menuProduct : menuProducts) {
            final Product product = productDao.findById(menuProduct.getProductId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("저장되지 않은 제품 ID 입니다.")
                    );
            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
        }
        BigDecimal menuPrice = menu.getPrice();
        if (menuPrice.compareTo(sum) > 0) {
            throw new IllegalArgumentException("메뉴가격은 제품가격의 총합보다 클 수 없습니다.");
        }
    }

    private void validatePrice(final BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("메뉴가격은 null 또는 음수일 수 없습니다.");
        }
    }

    private void validateMenuGroup(final Menu menu) {
        if (!menuGroupRepository.existsById(menu.getMenuGroupId())) {
            throw new IllegalArgumentException("저장되지 않은 메뉴그룹 ID 입니다.");
        }
    }
}
