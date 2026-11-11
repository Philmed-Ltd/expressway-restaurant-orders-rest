package io.salesorder.validation;

import io.lib.service.BaseJpaRepoReadService;
import io.salesorder.entity.SalesOrderPayment;
import io.salesorder.repository.SalesOrderPaymentRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UniqueTransactionIdValidator extends BaseJpaRepoReadService<SalesOrderPayment, SalesOrderPaymentRepository>
        implements ConstraintValidator<UniqueTransactionId, String> {

    @Override
    public boolean isValid(String transactionId, ConstraintValidatorContext constraintValidatorContext) {
        if (transactionId == null || transactionId.isBlank()) {
            return true;
        }
        Specification<SalesOrderPayment> spec = repository.notDeleted().and(repository.transactionIdIs(transactionId));

       return !repository.exists(spec);
    }
}
