package io.productcatalog.validator;

import io.productcatalog.entity.ProductStatus;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {ProductStatusUpdateValidator.class})
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)

public @interface ProductStatusUpdate {
    String message() default "Invalid status for this operation.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    ProductStatus[] status();
}
