package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.dto.request.ProductRequest;
import kitchenpos.dto.response.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productDao;

    public ProductService(final ProductRepository productDao) {
        this.productDao = productDao;
    }

    @Transactional
    public ProductResponse create(final ProductRequest productRequest) {
        validatePrice(productRequest);
        final Product product = productRequest.toEntity();
        final Product savedProduct = productDao.save(product);
        return new ProductResponse(savedProduct);
    }

    private void validatePrice(final ProductRequest productRequest) {
        final BigDecimal price = productRequest.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("제품의 가격은 null 또는 음수일 수 없습니다.");
        }
    }

    public List<ProductResponse> list() {
        final List<Product> products = productDao.findAll();
        return products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }
}
