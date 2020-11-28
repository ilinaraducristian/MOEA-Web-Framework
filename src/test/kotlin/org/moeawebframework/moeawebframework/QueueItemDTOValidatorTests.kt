package org.moeawebframework.moeawebframework

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.springframework.boot.test.context.SpringBootTest
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

val validatorFactory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
val validator: Validator = validatorFactory.validator

@SpringBootTest
class QueueItemDTOValidatorTests {

  companion object {

    @AfterAll
    @JvmStatic
    fun afterAll() {
      validatorFactory.close()
    }

  }

  @Test
  fun `all fields are ok`() {
    val queueItemDTO = QueueItemDTO(
        "An ok formatted name",
        10000,
        10,
        "MD5 or name",
        "MD5 or name",
        "MD5 or name"
    )
    val violations = validator.validate(queueItemDTO)
    assert(violations.isEmpty())
  }

}