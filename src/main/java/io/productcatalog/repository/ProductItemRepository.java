package io.productcatalog.repository;

import io.lib.repository.BaseJpaRepository;
import io.productcatalog.entity.ProductItem;
import io.productcatalog.entity.ProductStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductItemRepository extends BaseJpaRepository<ProductItem> {
    default Specification<ProductItem> nameIs(String name) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name));
    }

    default Specification<ProductItem> nameLike(String query) {
        return ((root, query1, criteriaBuilder) ->  criteriaBuilder.like(root.get("name"), "%" + query + "%"));
    }

    default Specification<ProductItem> productCategoryIdIs(String productCategoryId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("productCategory"), productCategoryId));
    }

    default Specification<ProductItem> productStatusIs(ProductStatus productStatus) {
        return ((root, cq, cb) -> cb.equal(root.get("productStatus"), productStatus));
    }
}
