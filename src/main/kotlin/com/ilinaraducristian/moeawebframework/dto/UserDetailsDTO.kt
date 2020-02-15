package com.ilinaraducristian.moeawebframework.dto

import com.ilinaraducristian.moeawebframework.entities.Algorithm
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.entities.QueueItem

class UserDetailsDTO {
  var username: String = ""
  var email: String = ""
  var firstName: String = ""
  var lastName: String? = null
  var jwt: String = ""
  var problems: MutableSet<Problem> = mutableSetOf()
  var algorithms: MutableSet<Algorithm> = mutableSetOf()
  var queue: MutableList<QueueItem> = ArrayList()
}