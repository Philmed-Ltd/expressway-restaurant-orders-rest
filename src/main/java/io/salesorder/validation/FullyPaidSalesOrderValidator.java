package io.salesorder.validation;

import io.salesorder.entity.SalesOrder;
import io.salesorder.service.SalesOrderReadService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
class FullyPaidSalesOrderValidator
        implements ConstraintValidator<FullyPaidSalesOrder, String> {
    private SalesOrderReadService salesOrderReadService;

    @Override
    public boolean isValid(String salesOrderId, ConstraintValidatorContext context) {

        SalesOrder salesOrder = salesOrderReadService.findByEntityId(salesOrderId);
        BigDecimal totalCharge = salesOrder.getTotalAmountToBePaid();
        BigDecimal amountPaid = salesOrder.getAmountPaid();
        if (totalCharge == null) { return false; }
        if (amountPaid == null) { return false; }
        return amountPaid.compareTo(totalCharge) >= 0;
    }

    @Autowired
    public void setSalesOrderReadService(SalesOrderReadService service) {
        this.salesOrderReadService = service;
    }
}
