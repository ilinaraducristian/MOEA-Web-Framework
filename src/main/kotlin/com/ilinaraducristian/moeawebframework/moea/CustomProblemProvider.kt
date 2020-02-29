package com.ilinaraducristian.moeawebframework.moea

import com.ilinaraducristian.moeawebframework.security.UserPrincipal
import org.moeaframework.core.NondominatedPopulation
import org.moeaframework.core.PopulationIO
import org.moeaframework.core.Problem
import org.moeaframework.core.spi.ProblemProvider
import org.springframework.security.core.context.SecurityContextHolder
import java.io.File
import java.net.MalformedURLException
import java.net.URLClassLoader
import java.security.Principal

class CustomProblemProvider : ProblemProvider() {

  @Suppress("DEPRECATION")
  override fun getProblem(name: String): Problem? {
    var problem: Problem? = null
    var foundProblem = File("moeaData/problems").list()?.find { problem ->
      problem == "$name.class"
    }
    if (foundProblem != null) {
      val file = File("moeaData/problems/$name.class")
      if (file.exists())
        try {
          problem = URLClassLoader(arrayOf(file.toURI().toURL())).loadClass(name).newInstance() as Problem
        } catch (e: MalformedURLException) {
          println("MalformedURLException")
        } catch (e: ClassNotFoundException) {
          println("ClassNotFoundException")
        }
    }
      return problem
  }

  override fun getReferenceSet(name: String): NondominatedPopulation? {
    var referenceSet: NondominatedPopulation? = null
    var foundReferenceSet = File("moeaData/problems/references").list()?.find { referenceSet ->
      referenceSet == "$name.pf"
    }
    if (foundReferenceSet != null) {
      referenceSet = NondominatedPopulation(PopulationIO.readObjectives(File("moeaData/problems/references/$name.pf")))
    }
    return referenceSet
  }
}