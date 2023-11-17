package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MenuRepositoryTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuProductRepository menuProductRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 저장된_메뉴_개수를_조회한다() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(new MenuGroup("메뉴그룹"));
        Menu menu1 = menuRepository.save(new Menu("메뉴1", BigDecimal.TEN, menuGroup.getId(), null));
        Menu menu2 = menuRepository.save(new Menu("메뉴2", BigDecimal.TEN, menuGroup.getId(), null));

        // when
        List<Long> menuIds = Arrays.asList(menu1.getId(), menu2.getId());
        Long countOfMenu = menuRepository.countByIdIn(menuIds);

        // then
        Assertions.assertThat(countOfMenu).isEqualTo(2L);
    }

    @Test
    void findAll_의_n_플러스_1_문제_해결() {
        // given
        MenuGroup menuGroup = new MenuGroup("메뉴그룹");
        menuGroupRepository.save(menuGroup);
        Menu menu = new Menu("메뉴1", BigDecimal.valueOf(1000), menuGroup.getId(), null);
        menuRepository.save(menu);
        Product product1 = new Product("제품1", BigDecimal.valueOf(500));
        Product product2 = new Product("제품2", BigDecimal.valueOf(500));
        productRepository.save(product1);
        productRepository.save(product2);
        MenuProduct menuProduct1 = new MenuProduct(null, menu.getId(), product1.getId(), 1L);
        MenuProduct menuProduct2 = new MenuProduct(null, menu.getId(), product2.getId(), 1L);
        menuProductRepository.save(menuProduct1);
        menuProductRepository.save(menuProduct2);
        entityManager.clear();

        // when
        List<Menu> totalMenu = menuRepository.findAll();

        // then
        Assertions.assertThat(totalMenu.size()).isEqualTo(1);
    }
}
