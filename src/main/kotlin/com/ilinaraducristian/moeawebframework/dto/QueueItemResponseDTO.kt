package com.ilinaraducristian.moeawebframework.dto

import java.io.Serializable

class QueueItemResponseDTO : Serializable{

  var name: String = ""
  var numberOfEvaluations: Int = 10000
  var numberOfSeeds: Int = 10
  var status: String = "waiting"
  var rabbitId: String = ""
  var results: ArrayList<QualityIndicators> = ArrayList()
  var problem: String = ""
  var algorithm: String = ""

}