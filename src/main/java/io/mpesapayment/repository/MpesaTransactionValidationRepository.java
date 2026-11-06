package io.mpesapayment.repository;


import io.lib.repository.BaseJpaRepository;
import io.mpesapayment.entity.MpesaTransactionValidation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaTransactionValidationRepository extends BaseJpaRepository<MpesaTransactionValidation> {

    default Specification<MpesaTransactionValidation> originatorConversationIdIs(String id){
        return ((root, cq, cb) -> cb.equal(root.get("originatorConversationId"), id));
    }
}
