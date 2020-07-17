package com.ilinaraducristian.moeawebframework.validators

import com.ilinaraducristian.moeawebframework.constraints.ProblemSolverNameConstraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class ProblemSolverNameValidator : ConstraintValidator<ProblemSolverNameConstraint, String> {

  override fun initialize(value: ProblemSolverNameConstraint) {}

  override fun isValid(value: String,
                       cxt: ConstraintValidatorContext): Boolean {
    var error = true
    try {
      Integer.parseInt(value.trim()[0].toString())
    } catch (e: Exception) {
      error = false
    }
    return !error
  }
}