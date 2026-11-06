package io.salesorder.repository;

import io.lib.repository.BaseJpaRepository;
import io.salesorder.entity.SalesOrder;
import org.springframework.data.jpa.domain.Specification;

public interface SalesOrderRepository extends BaseJpaRepository<SalesOrder> {

    default Specification<SalesOrder> customerIdIs(String customerEntityId) {
        return (root, query, cb) -> cb.equal(
                root.get("customer").get("entityId"), customerEntityId
        );
    }

}
