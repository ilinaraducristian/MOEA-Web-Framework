package com.ilinaraducristian.moeawebframework

import com.ilinaraducristian.moeawebframework.configurations.TestRedisConfiguration
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [TestRedisConfiguration::class])
class SessionIntegrationTest {

}