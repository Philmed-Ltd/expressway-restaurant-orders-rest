package io.salesorder.repository;

import io.lib.repository.BaseJpaRepository;
import io.salesorder.entity.SaleOrderItem;
import org.springframework.data.jpa.domain.Specification;

public interface SaleOrderItemRepository extends BaseJpaRepository<SaleOrderItem> {

    default Specification<SaleOrderItem> byOrderId(String orderId) {
        return (root, query, cb) -> cb.equal(root.get("salesOrderEntityId"), orderId);
    }

}
