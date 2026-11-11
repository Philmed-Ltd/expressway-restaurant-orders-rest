package io.mpesapayment.repository;

import io.lib.repository.BaseJpaRepository;
import io.mpesapayment.entity.MpesaSTKPayment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaSTKPaymentRepository extends BaseJpaRepository<MpesaSTKPayment> {
    default Specification<MpesaSTKPayment> checkoutRequestIdIs(String checkoutRequestId){
        return (root, cq, cb) -> cb.equal(root.get("checkoutRequestId"), checkoutRequestId);
    }
    default Specification<MpesaSTKPayment> transactionIdIs(String transactionId) {
        return (root, cq, cb) -> cb.equal(root.get("transactionId"), transactionId);
    }
}
