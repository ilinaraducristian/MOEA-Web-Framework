package com.ilinaraducristian.moeawebframework.validators

import com.ilinaraducristian.moeawebframework.constraints.QueueItemNameConstraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class QueueItemNameValidator : ConstraintValidator<QueueItemNameConstraint, String> {

  override fun initialize(value: QueueItemNameConstraint) {}

  override fun isValid(value: String,
                       cxt: ConstraintValidatorContext): Boolean {
    var error = true
    try{
      Integer.parseInt(value.trim()[0].toString())
    }catch(e: Exception){
      error = false
    }
    return !error
  }
}