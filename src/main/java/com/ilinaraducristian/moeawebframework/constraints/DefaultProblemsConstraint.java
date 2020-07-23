package com.ilinaraducristian.moeawebframework.constraints;

import com.ilinaraducristian.moeawebframework.validators.DefaultProblemsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DefaultProblemsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultProblemsConstraint {
    String message() default "problem not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
