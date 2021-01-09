package org.moeawebframework.moeawebframework.dto

import org.moeawebframework.moeawebframework.entities.QueueItem

data class QueueItemResponseDTO(

    var name: String = "",

    var numberOfEvaluations: Int = 0,

    var numberOfSeeds: Int = 0,

    var status: String = "waiting",

    var rabbitId: String = "",

    var results: String = "",

    var algorithmMD5: String = "",

    var problemMD5: String = "",

    var referenceSetMD5: String = ""

) {

  constructor(queueItem: QueueItem) : this(
      queueItem.name,
      queueItem.numberOfEvaluations,
      queueItem.numberOfSeeds,
      queueItem.status,
      queueItem.rabbitId,
      queueItem.results,
      queueItem.algorithmMD5,
      queueItem.problemMD5,
      queueItem.referenceSetMD5
  )

}