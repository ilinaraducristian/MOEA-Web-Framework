package org.moeawebframework.moeawebframework.configs

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories


@Configuration
@EnableR2dbcRepositories(basePackages = ["org.moeawebframework.moeawebframework.repositories"])
class R2DBCConfig : AbstractR2dbcConfiguration() {

//  @Bean
  override fun connectionFactory(): ConnectionFactory {
    return ConnectionFactories.get("r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
  }

//  var client = DatabaseClient.create(connectionFactory)

}
