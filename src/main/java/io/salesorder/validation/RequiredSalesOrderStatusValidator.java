package io.salesorder.validation;

import io.salesorder.entity.OrderStatus;
import io.salesorder.service.SalesOrderReadService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RequiredSalesOrderStatusValidator
        implements ConstraintValidator<RequiredSalesOrderStatus, String> {

    private List<OrderStatus> statuses;
    private SalesOrderReadService salesOrderReadService;

    @Override
    public void initialize(RequiredSalesOrderStatus annotation) {
        ConstraintValidator.super.initialize(annotation);
        this.statuses = List.of(annotation.statuses());
    }

    @Override
    public boolean isValid(String salesOrderId, ConstraintValidatorContext ctx) {
        if (salesOrderId == null || salesOrderId.isBlank()) {
            return true;
        }
        var salesOrder = salesOrderReadService.findByEntityId(salesOrderId);
        return salesOrder != null && statuses.contains(salesOrder.getOrderStatus());
    }

    @Autowired
    public void setSalesOrderReadService(SalesOrderReadService service) {
        this.salesOrderReadService = service;
    }
}
