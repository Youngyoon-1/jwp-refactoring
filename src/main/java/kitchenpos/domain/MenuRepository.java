package kitchenpos.domain;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @EntityGraph(attributePaths = "menuProducts")
    List<Menu> findAll();

    long countByIdIn(List<Long> ids);
}
