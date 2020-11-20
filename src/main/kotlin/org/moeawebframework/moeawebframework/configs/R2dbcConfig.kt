package org.moeawebframework.moeawebframework.configs

import io.r2dbc.h2.H2ConnectionConfiguration
import io.r2dbc.h2.H2ConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
class R2dbcConfig : AbstractR2dbcConfiguration() {

  @Bean
  @Profile("test")
  override fun connectionFactory(): ConnectionFactory {
    val connectionFactory = H2ConnectionFactory(H2ConnectionConfiguration.builder()
        .inMemory("moeawebframework")
        .option("DB_CLOSE_DELAY=-1")
        .option("DATABASE_TO_UPPER=FALSE")
        .build())
    val initializer = ConnectionFactoryInitializer()
    initializer.setConnectionFactory(connectionFactory)
    val compositeDatabasePopulator = CompositeDatabasePopulator()
    compositeDatabasePopulator.addPopulators(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
    compositeDatabasePopulator.addPopulators(ResourceDatabasePopulator(ClassPathResource("data.sql")))
    initializer.setDatabasePopulator(compositeDatabasePopulator)
    initializer.afterPropertiesSet()
    return connectionFactory
  }

}
