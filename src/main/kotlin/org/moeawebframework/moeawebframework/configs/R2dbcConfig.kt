package org.moeawebframework.moeawebframework.configs

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration

@Configuration
class R2dbcConfig : AbstractR2dbcConfiguration() {

  @Value("\${R2DBC_URI}")
  lateinit var R2DBC_URI: String

  override fun connectionFactory(): ConnectionFactory {
    return ConnectionFactories.get(R2DBC_URI)
  }

//  @Bean
//  @Profile("dev")
//  fun initializer(): ConnectionFactoryInitializer {
//    val initializer = ConnectionFactoryInitializer()
//    initializer.setConnectionFactory(connectionFactory())
//    val populator = CompositeDatabasePopulator()
//    populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
//    populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("data.sql")))
//    initializer.setDatabasePopulator(populator)
//    return initializer
//  }

}
