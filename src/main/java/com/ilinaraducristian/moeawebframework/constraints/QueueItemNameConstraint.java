package com.ilinaraducristian.moeawebframework.constraints;

import com.ilinaraducristian.moeawebframework.validators.QueueItemNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = QueueItemNameValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface QueueItemNameConstraint {
  String message() default "name must not start with a number";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}