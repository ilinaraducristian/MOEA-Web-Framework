package org.moeawebframework.moeawebframework.configs

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator

@Configuration
@Profile("dev")
class R2dbcConfig {

  @Autowired
  @Qualifier("connectionFactory")
  lateinit var connectionFactory: ConnectionFactory

  @Bean
  @Profile("dev")
  fun initializer(): ConnectionFactoryInitializer {
    val initializer = ConnectionFactoryInitializer()
    initializer.setConnectionFactory(connectionFactory)
    val populator = CompositeDatabasePopulator()
    populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
    populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("data.sql")))
    initializer.setDatabasePopulator(populator)
    return initializer

  }

}
