package org.moeawebframework.configs


import dev.miku.r2dbc.mysql.MySqlConnectionFactoryProvider
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@TestConfiguration
class R2DBCTestConfig : AbstractR2dbcConfiguration() {

//  @Bean
//  @Profile("test")
//  override fun connectionFactory(): ConnectionFactory {
//      val asd = MySqlConnectionFactory.from(MySqlConnectionConfiguration.builder()
//          .database("moeawebframework")
//          .build())
//    val connectionFactory = H2ConnectionFactory(H2ConnectionConfiguration.builder()
//        .inMemory("moeawebframework")
//        .option("DB_CLOSE_DELAY=-1")
//        .option("DATABASE_TO_UPPER=FALSE")
//        .build())
//    val initializer = ConnectionFactoryInitializer()
//    initializer.setConnectionFactory(connectionFactory)
//    val compositeDatabasePopulator = CompositeDatabasePopulator()
//    compositeDatabasePopulator.addPopulators(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
//    compositeDatabasePopulator.addPopulators(ResourceDatabasePopulator(ClassPathResource("data.sql")))
//    initializer.setDatabasePopulator(compositeDatabasePopulator)
//    initializer.afterPropertiesSet()
//    return connectionFactory
//  }

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val connectionFactory =
            MySqlConnectionFactoryProvider().create(ConnectionFactoryOptions.parse("r2dbc:mysql://root:root@localhost:3306/moeawebframework"))
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        initializer.setDatabasePopulator(
            ResourceDatabasePopulator(
                ClassPathResource("schema.sql")
            )
        )
        initializer.afterPropertiesSet()
        return connectionFactory
    }

}