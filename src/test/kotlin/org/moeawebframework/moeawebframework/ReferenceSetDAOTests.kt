package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dao.ReferenceSetDAO
import org.moeawebframework.moeawebframework.entities.ReferenceSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestConfig::class)
class ReferenceSetDAOTests {

  @Autowired
  lateinit var referenceSetDAO: ReferenceSetDAO

  @Test
  fun `should add, return and delete an referenceSet`() = runBlocking {
    val newReferenceSet = ReferenceSet(null, "ReferenceSetName", "ba91585ae7d736069b77f153763e857a")
    val referenceSet = referenceSetDAO.save(newReferenceSet)
    assertEquals(referenceSet, referenceSetDAO.get(referenceSet?.id!!))
    referenceSetDAO.delete(referenceSet)

  }

  @Test
  fun `should return null if non existent`() = runBlocking {
    assertNull(referenceSetDAO.get(100))
  }

  @Test
  fun `should return a list with existing referenceSets that belong to this user`() = runBlocking {
    assertNotEquals(0, referenceSetDAO.getByUserEntityId("cdd36e48-f1c5-474e-abc3-ac7a17909878").size)
  }

  @Test
  fun `should return an empty list for an existing user with no referenceSets`() = runBlocking {
    assertEquals(0, referenceSetDAO.getByUserEntityId("cdd36e48-f1c5-474e-abc3-ac7a17909879").size)
  }

  @Test
  fun `should return an empty list for a non existing user`() = runBlocking {
    assertEquals(0, referenceSetDAO.getByUserEntityId("371ac96d-a0b0-4bb0-901d-46cecc31ce1b").size)
  }

  @Test
  fun `should return an referenceSet that belogs to this user by MD5`() = runBlocking {
    assertNotNull(referenceSetDAO.getByUserEntityIdAndMD5("cdd36e48-f1c5-474e-abc3-ac7a17909878", "c835346a50b0d2335483bb0d6e439df0"))
  }

}

