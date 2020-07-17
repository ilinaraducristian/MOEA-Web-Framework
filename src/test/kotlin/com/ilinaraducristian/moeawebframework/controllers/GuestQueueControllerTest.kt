package com.ilinaraducristian.moeawebframework.controllers

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(GuestQueueController::class)
class GuestQueueControllerTest {

  @Autowired
  lateinit var mvc: MockMvc

  @Test
  fun whenValidInput_thenReturns200() {

  }

}