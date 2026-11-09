package io.salesorder.repository;

import io.lib.repository.BaseJpaRepository;
import io.salesorder.entity.SalesOrderPayment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderPaymentRepository extends BaseJpaRepository<SalesOrderPayment> {

    default Specification<SalesOrderPayment> salesOrderEntityIdIs(String salesOrderId) {
        return (root, cb, cq) -> cq.equal(root.get("salesOrderEntityId"), salesOrderId);
    }
    default Specification <SalesOrderPayment> transactionIdIs(String transactionId){
        return (root, query, cb) -> cb.equal(root.get("transactionId") , transactionId);
    }

}