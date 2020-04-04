package com.ilinaraducristian.moeawebframework.dto

import com.ilinaraducristian.moeawebframework.entities.QueueItem

class UserDetailsDTO {
  var username: String = ""
  var email: String = ""
  var firstName: String = ""
  var lastName: String? = null
  var jwt: String = ""
  var problems: ArrayList<String> = ArrayList()
  var algorithms: ArrayList<String> = ArrayList()
  var queue: MutableList<QueueItem> = ArrayList()
}