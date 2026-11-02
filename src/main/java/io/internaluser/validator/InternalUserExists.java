package io.internaluser.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = {
        InternalUserExistsSingleIdValidator.class,
        InternalUserExistsMultipleIdsValidator.class
})
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface InternalUserExists {
    String message() default "error.internal.user.not.exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
