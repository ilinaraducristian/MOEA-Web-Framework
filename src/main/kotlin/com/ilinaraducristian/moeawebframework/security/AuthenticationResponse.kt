package com.ilinaraducristian.moeawebframework.security

import com.ilinaraducristian.moeawebframework.dto.QueueItemResponseDTO
import java.io.Serializable

class AuthenticationResponse : Serializable {

  var username: String = ""
  var email: String = ""
  var firstName: String = ""
  var lastName: String? = null
  var jwt: String = ""
  var problems: List<String> = mutableListOf()
  var algorithms: List<String> = mutableListOf()
  var queue: MutableList<QueueItemResponseDTO> = ArrayList()

}