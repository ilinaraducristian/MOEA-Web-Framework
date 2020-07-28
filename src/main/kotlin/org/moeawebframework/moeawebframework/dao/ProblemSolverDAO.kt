package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.ProblemSolver
import org.moeawebframework.moeawebframework.repositories.ProblemSolverRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.RuntimeException

@Repository
class ProblemSolverDAO(
    private val problemSolverRepository: ProblemSolverRepository
) : Dao<ProblemSolver> {
  override fun get(id: Long): Mono<ProblemSolver> {
    return problemSolverRepository.findById(id)
  }

  override fun getAll(): Flux<ProblemSolver> {
    return problemSolverRepository.findAll()
  }

  override fun save(t: ProblemSolver): Mono<ProblemSolver> {
    return problemSolverRepository.save(t)
  }

  override fun update(t: ProblemSolver, fields: HashMap<String, Any?>): Mono<Void> {
    var modified = false
    if(fields.containsKey("status")) {
      if(fields["status"] == null) return Mono.error(RuntimeException("Status cannot be null"))
      t.status = fields["status"] as String
      modified = true
    }
    if(modified) {
      return save(t).flatMap { Mono.empty<Void>() }
    }else {
      return Mono.empty()
    }
  }

  override fun delete(t: ProblemSolver): Mono<Void> {
    return problemSolverRepository.delete(t)
  }


}