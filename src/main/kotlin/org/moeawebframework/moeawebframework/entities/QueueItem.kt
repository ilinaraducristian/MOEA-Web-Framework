package org.moeawebframework.moeawebframework.entities

import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("queue_items")
data class QueueItem(

    @Id
    var id: Long? = null,

    var name: String = "",

    var numberOfEvaluations: Int = 0,

    var numberOfSeeds: Int = 0,

    var status: String = "waiting",

    var rabbitId: String = "",

    var results: String = "",

    var algorithmMD5: String = "",

    var problemMD5: String = "",

    var referenceSetMD5: String = "",

    var userId: Long? = null

) {

  constructor(processDTO: QueueItemDTO, uuid: String) : this(
      null,
      processDTO.name,
      processDTO.numberOfEvaluations,
      processDTO.numberOfSeeds,
      "waiting",
      uuid,
      "",
      processDTO.algorithmMD5,
      processDTO.problemMD5,
      processDTO.referenceSetMD5
  )

}

