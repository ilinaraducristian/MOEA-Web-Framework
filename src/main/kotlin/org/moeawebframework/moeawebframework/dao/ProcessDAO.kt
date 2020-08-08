package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.Process
import org.moeawebframework.moeawebframework.repositories.ProcessRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class ProcessDAO(
    private val processRepository: ProcessRepository
) : DAO<Process> {
  override fun get(id: Long): Mono<Process> {
    return processRepository.findById(id)
  }

  override fun getAll(): Flux<Process> {
    return processRepository.findAll()
  }

  override fun save(t: Process): Mono<Process> {
    return processRepository.save(t)
  }

  override fun update(t: Process, fields: HashMap<String, Any?>): Mono<Void> {
    var modified = false
    if (fields.containsKey("status")) {
      if (fields["status"] == null) return Mono.error(RuntimeException("Status cannot be null"))
      t.status = fields["status"] as String
      modified = true
    }
    if (modified) {
      return save(t).flatMap { Mono.empty<Void>() }
    } else {
      return Mono.empty()
    }
  }

  override fun delete(t: Process): Mono<Void> {
    return processRepository.delete(t)
  }

  fun getByRabbitId(rabbitId: String): Mono<Process> {
    return processRepository.findByRabbitId(rabbitId)
  }

}