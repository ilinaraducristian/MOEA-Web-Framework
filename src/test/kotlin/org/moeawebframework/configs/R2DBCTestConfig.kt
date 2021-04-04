package org.moeawebframework.configs

import dev.miku.r2dbc.mysql.MySqlConnectionFactoryProvider
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@TestConfiguration
class R2DBCTestConfig : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val connectionFactory =
            MySqlConnectionFactoryProvider().create(ConnectionFactoryOptions.parse("r2dbc:mysql://root:root@localhost:3306/moeawebframework"))
        val initializer = ConnectionFactoryInitializer()
        val schemaPopulator = ResourceDatabasePopulator(ClassPathResource("schema.sql"))
        schemaPopulator.setSeparator("$$")
        val dataPopulator = ResourceDatabasePopulator(ClassPathResource("data.sql"))
        val databasePopulator = CompositeDatabasePopulator(listOf(schemaPopulator, dataPopulator))
        initializer.setConnectionFactory(connectionFactory)
        initializer.setDatabasePopulator(databasePopulator)
        initializer.afterPropertiesSet()
        return connectionFactory
    }

}