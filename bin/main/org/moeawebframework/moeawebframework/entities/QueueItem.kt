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

    var userEntityId: String? = null

) {

  constructor(queueItemDTO: QueueItemDTO, uuid: String, userEntityId: String) : this(
      null,
      queueItemDTO.name,
      queueItemDTO.numberOfEvaluations,
      queueItemDTO.numberOfSeeds,
      "waiting",
      uuid,
      "",
      queueItemDTO.algorithmMD5,
      queueItemDTO.problemMD5,
      queueItemDTO.referenceSetMD5,
      userEntityId
  )

}

