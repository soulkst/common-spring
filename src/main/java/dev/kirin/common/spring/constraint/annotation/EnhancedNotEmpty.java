package dev.kirin.common.spring.constraint.annotation;

import dev.kirin.common.spring.constraint.validator.EnhancedNotEmptyConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnhancedNotEmptyConstraintValidator.class)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnhancedNotEmpty {
    String message() default "{javax.validation.constraints.NotEmpty.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
