package com.ilinaraducristian.moeawebframework.constraints;

import com.ilinaraducristian.moeawebframework.validators.ProblemSolverNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProblemSolverNameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProblemSolverNameConstraint {
    String message() default "name must not start with a number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}