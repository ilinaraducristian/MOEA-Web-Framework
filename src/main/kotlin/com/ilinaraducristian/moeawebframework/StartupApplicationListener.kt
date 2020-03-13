package com.ilinaraducristian.moeawebframework

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.controllers.algorithms
import com.ilinaraducristian.moeawebframework.controllers.problems
import com.ilinaraducristian.moeawebframework.entities.*
import com.ilinaraducristian.moeawebframework.exceptions.CannotCreateUserException
import com.ilinaraducristian.moeawebframework.moea.CustomAlgorithmProvider
import com.ilinaraducristian.moeawebframework.moea.CustomProblemProvider
import com.ilinaraducristian.moeawebframework.repositories.AlgorithmRepository
import com.ilinaraducristian.moeawebframework.repositories.AuthorityRepository
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.security.SecurityUserDetailsService
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
import org.moeaframework.core.spi.AlgorithmFactory
import org.moeaframework.core.spi.ProblemFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.io.File

@Component
class StartupApplicationListener(
    private val userRepo: UserRepository,
    private val authorityRepo: AuthorityRepository,
    private val encoder: BCryptPasswordEncoder,
    private val queueItemSolverService: QueueItemSolverService
) : ApplicationListener<ContextRefreshedEvent> {

  @Value("\${guest.password}")
  private val guestPassword = ""

  override fun onApplicationEvent(event: ContextRefreshedEvent) {
    if(!File("moeaData").exists()) File("moeaData").mkdir()
    if(userRepo.findByUsername("guest").isEmpty) {
      val user = User()
      user.username = "guest"
      user.password = encoder.encode(guestPassword)
      user.email = "guest"
      user.firstName = "guest"
      problems.forEach { name ->
        val tmp = Problem(name = name)
        user.problems.add(tmp)
        tmp.users.add(user)
      }
      algorithms.forEach { name ->
        val tmp = Algorithm(name = name)
        user.algorithms.add(tmp)
        tmp.users.add(user)
      }
      try {
        authorityRepo.save(Authority(user = userRepo.save(user)))
      } catch (e: Exception) {
      }
    }
    val users = userRepo.findAll()

    users.forEach {user ->
      ProblemFactory.getInstance().addProvider(CustomProblemProvider(user.username))
      AlgorithmFactory.getInstance().addProvider(CustomAlgorithmProvider(user.username))
    }

  }

}