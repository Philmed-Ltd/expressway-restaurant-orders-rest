package io.salesorder.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {UniqueTransactionIdValidator.class})
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueTransactionId {
    String message() default "payment with transaction id already exists.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
