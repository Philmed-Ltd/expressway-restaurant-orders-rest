package io.salesorder.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = { FullyPaidSalesOrderValidator.class })
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FullyPaidSalesOrder {
    String message() default "error.order.not.fully.paid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}