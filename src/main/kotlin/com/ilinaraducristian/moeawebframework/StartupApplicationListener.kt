package com.ilinaraducristian.moeawebframework

import com.ilinaraducristian.moeawebframework.moea.CustomProblemProvider
import org.moeaframework.core.spi.ProblemFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class StartupApplicationListener: ApplicationListener<ContextRefreshedEvent> {

  override fun onApplicationEvent(event: ContextRefreshedEvent) {
    ProblemFactory.getInstance().addProvider(CustomProblemProvider())
  }

}