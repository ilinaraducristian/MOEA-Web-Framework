package com.ilinaraducristian.moeawebframework.validators

import com.ilinaraducristian.moeawebframework.constraints.DefaultAlgorithmsConstraint
import com.ilinaraducristian.moeawebframework.configurations.algorithms
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class DefaultAlgorithmsValidator : ConstraintValidator<DefaultAlgorithmsConstraint, String> {

  override fun initialize(value: DefaultAlgorithmsConstraint) {}

  override fun isValid(value: String,
                       cxt: ConstraintValidatorContext): Boolean {
    return algorithms.contains(value)
  }
}