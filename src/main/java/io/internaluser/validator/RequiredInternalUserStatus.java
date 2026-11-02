package io.internaluser.validator;


import io.user.entity.UserStatus;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {
    RequiredUserStatusSingleIsValidator.class,
    RequiredUserStatusMultipleIdsValidator.class
})
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredInternalUserStatus {
    String message() default "error.invalid.status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    UserStatus[] statuses();
}
