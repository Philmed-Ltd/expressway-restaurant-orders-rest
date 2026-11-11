package io.salesorder.validation;

import io.salesorder.entity.OrderStatus;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {RequiredSalesOrderStatusValidator.class})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredSalesOrderStatus {
    String message() default "error.sales.order.invalid.status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    OrderStatus[] statuses();
}
