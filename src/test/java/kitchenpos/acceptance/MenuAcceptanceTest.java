package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.dto.request.MenuRequest;
import kitchenpos.dto.response.MenuProductResponse;
import kitchenpos.dto.response.MenuResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@AcceptanceTest
public class MenuAcceptanceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 메뉴를_한_개_등록한다() {
        // given
        MenuGroup menuGroup = new MenuGroup("메뉴그룹이름");
        menuGroupRepository.save(menuGroup);
        Product product = new Product("제품이름", BigDecimal.valueOf(1000));
        productRepository.save(product);
        MenuProduct menuProduct = new MenuProduct(product.getId(), 1L);
        List<MenuProduct> menuProducts = Collections.singletonList(menuProduct);
        MenuRequest request = new MenuRequest("메뉴이름", BigDecimal.valueOf(1000), menuGroup.getId(), menuProducts);

        // when
        ResponseEntity<MenuResponse> response = testRestTemplate.postForEntity(
                "/api/menus",
                request,
                MenuResponse.class
        );

        // then
        MenuResponse menuResponse = response.getBody();
        long menuId = menuResponse.getId();
        String menuName = menuResponse.getName();
        BigDecimal menuPrice = menuResponse.getPrice();
        long menuGroupId = menuResponse.getMenuGroupId();
        List<MenuProductResponse> menuProductsResponse = menuResponse.getMenuProducts();
        MenuProductResponse menuProductResponse = menuProductsResponse.get(0);
        long menuProductId = menuProductResponse.getSeq();
        long menuIdOfMenuProduct = menuProductResponse.getMenuId();
        long productId = menuProductResponse.getProductId();
        long quantity = menuProductResponse.getQuantity();
        assertAll(
                () -> assertThat(menuId).isNotNull(),
                () -> assertThat(menuName).isEqualTo("메뉴이름"),
                () -> assertThat(menuPrice).isEqualTo(BigDecimal.valueOf(1000)),
                () -> assertThat(menuGroupId).isNotNull(),
                () -> assertThat(menuProductId).isNotNull(),
                () -> assertThat(menuIdOfMenuProduct).isEqualTo(menuId),
                () -> assertThat(productId).isNotNull(),
                () -> assertThat(quantity).isOne()
        );
    }

    @Test
    void 메뉴를_한_개_저장한_뒤_전체를_조회한다() {
        // given
        MenuGroup menuGroup = new MenuGroup("메뉴그룹이름");
        menuGroupRepository.save(menuGroup);
        Product product = new Product("제품이름", BigDecimal.valueOf(1000));
        productRepository.save(product);
        MenuProduct menuProduct = new MenuProduct(product.getId(), 1L);
        List<MenuProduct> menuProducts = Collections.singletonList(menuProduct);
        MenuRequest request = new MenuRequest("메뉴이름", BigDecimal.valueOf(1000), menuGroup.getId(), menuProducts);

        // when
        ResponseEntity<MenuResponse> responseToSave = testRestTemplate.postForEntity(
                "/api/menus",
                request,
                MenuResponse.class
        );

        ResponseEntity<List<MenuResponse>> responseToSelect = testRestTemplate.exchange(
                "/api/menus",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MenuResponse>>() {
                }
        );

        // then
        MenuResponse menuResponseToSave = responseToSave.getBody();
        MenuResponse menuResponseToSelect = responseToSelect.getBody()
                .get(0);
        List<MenuProductResponse> menuProductResponsesToSave = menuResponseToSave.getMenuProducts();
        List<MenuProductResponse> menuProductResponsesToSelect = menuResponseToSelect.getMenuProducts();
        long priceToSave = menuResponseToSave.getPrice()
                .longValue();
        long priceToSelect = menuResponseToSelect.getPrice()
                .longValue();

        assertAll(
                () -> Assertions.assertThat(menuResponseToSave)
                        .isEqualToIgnoringGivenFields(menuResponseToSelect, "price", "menuProducts"),
                () -> Assertions.assertThat(menuProductResponsesToSave).usingRecursiveComparison()
                        .isEqualTo(menuProductResponsesToSelect),
                () -> Assertions.assertThat(priceToSave).isEqualTo(priceToSelect)
        );
    }
}
