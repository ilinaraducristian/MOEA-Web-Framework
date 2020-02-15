package com.ilinaraducristian.moeawebframework.security

import com.ilinaraducristian.moeawebframework.entities.Algorithm
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import java.io.Serializable

class AuthenticationResponse : Serializable {

  var username: String = ""
  var email: String = ""
  var firstName: String = ""
  var lastName: String? = null
  var jwt: String = ""
  var problems: List<String> = mutableListOf()
  var algorithms: List<String> = mutableListOf()
  var queue: MutableList<QueueItem> = ArrayList()

}