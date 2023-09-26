package kitchenpos.dto.response;

import java.util.List;
import kitchenpos.domain.Product;

public class ProductsResponse {

    private List<ProductResponse> productResponses;

    public ProductsResponse(final List<Product> products) {
    }
}
