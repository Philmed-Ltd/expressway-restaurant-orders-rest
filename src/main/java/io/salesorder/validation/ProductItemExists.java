package io.salesorder.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProductItemExistsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductItemExists {
    String message() default "error.product.does.not.exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
