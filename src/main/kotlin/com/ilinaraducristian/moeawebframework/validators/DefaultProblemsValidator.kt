package com.ilinaraducristian.moeawebframework.validators

import com.ilinaraducristian.moeawebframework.constraints.DefaultProblemsConstraint
import com.ilinaraducristian.moeawebframework.controllers.problems
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class DefaultProblemsValidator : ConstraintValidator<DefaultProblemsConstraint, String> {

  override fun initialize(value: DefaultProblemsConstraint) {}

  override fun isValid(value: String,
                       cxt: ConstraintValidatorContext): Boolean {
    return problems.contains(value)
  }
}