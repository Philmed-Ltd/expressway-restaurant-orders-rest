package io.salesorder.repository;

import io.lib.repository.BaseJpaRepository;
import io.salesorder.entity.SaleOrderItem;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public interface SaleOrderItemRepository extends BaseJpaRepository<SaleOrderItem> {

    default Specification<SaleOrderItem> byOrderId(String orderId) {
        return (root, query, cb) -> cb.equal(root.get("entityId"), orderId);
    }

    default Specification<SaleOrderItem> byOrderIds(Collection<String> orderIds) {
        return (root, query, cb) -> root.get("entityId").in(orderIds);
    }
}
