package com.ilinaraducristian.moeawebframework.constraints;

import com.ilinaraducristian.moeawebframework.validators.DefaultAlgorithmsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DefaultAlgorithmsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultAlgorithmsConstraint {
    String message() default "algorithm not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}