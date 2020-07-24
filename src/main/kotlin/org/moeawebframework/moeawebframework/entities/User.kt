package org.moeawebframework.moeawebframework.entities

import org.springframework.data.annotation.Id

class User {
  @Id
  var id: Int? = null
  var username: String? = null
}