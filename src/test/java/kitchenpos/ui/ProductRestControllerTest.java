package kitchenpos.ui;

import static kitchenpos.support.fixture.ProductFixture.PRODUCT_1;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Collections;
import java.util.List;
import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
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
class ProductRestControllerTest extends UiTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductRestController productRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = setupMockMvc(productRestController);
    }

    @Test
    void 제품을_등록한다() throws Exception {
        // given
        Product productRequest = PRODUCT_1.생성();
        Product productResponse = PRODUCT_1.생성(1L);
        BDDMockito.given(productService.create(ArgumentMatchers.any(Product.class)))
                .willReturn(productResponse);

        // when
        String serializedRequestContent = getObjectMapper().writeValueAsString(productRequest);
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedRequestContent)
        );

        // then
        String serializedResponseContent = getObjectMapper().writeValueAsString(productResponse);
        assertAll(
                () -> resultActions.andExpect(
                        ResultMatcher.matchAll(
                                MockMvcResultMatchers.status().isCreated(),
                                MockMvcResultMatchers.header().string("location", "/api/products/1"),
                                MockMvcResultMatchers.content().string(serializedResponseContent)
                        )
                ),
                () -> BDDMockito.verify(productService).create(ArgumentMatchers.any(Product.class))
        );
    }

    @Test
    void 제품_전체를_조회한다() throws Exception {
        // given
        Product product = PRODUCT_1.생성();
        List<Product> response = Collections.singletonList(product);
        BDDMockito.given(productService.list())
                .willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/products")
        );

        // then
        String serializedContent = getObjectMapper().writeValueAsString(response);
        assertAll(
                () -> resultActions.andExpect(
                        ResultMatcher.matchAll(
                                MockMvcResultMatchers.status().isOk(),
                                MockMvcResultMatchers.content().string(serializedContent)
                        )
                ),
                () -> BDDMockito.verify(productService).list()
        );
    }
}
