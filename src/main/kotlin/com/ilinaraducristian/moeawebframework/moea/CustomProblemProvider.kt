package com.ilinaraducristian.moeawebframework.moea

import org.moeaframework.core.NondominatedPopulation
import org.moeaframework.core.PopulationIO
import org.moeaframework.core.Problem
import org.moeaframework.core.spi.ProblemProvider
import org.moeaframework.util.TypedProperties
import java.io.File
import java.net.MalformedURLException
import java.net.URLClassLoader
import java.util.*

class CustomProblemProvider(
    private val username: String
) : ProblemProvider() {

  @Suppress("DEPRECATION")
  override fun getProblem(name: String): Problem? {
    var problem: Problem? = null
    val foundProblem = File("moeaData/$username/problems/").list()?.find { problem ->
      problem == "$name.class"
    }
    if (foundProblem != null) {
      val file = File("moeaData/$username/problems/$name.class")
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
    var foundReferenceSet = File("moeaData/$username/problems/references").list()?.find { referenceSet ->
      referenceSet == "$name.pf"
    }
    if (foundReferenceSet != null) {
      referenceSet = NondominatedPopulation(PopulationIO.readObjectives(File("moeaData/$username/problems/references/$name.pf")))
    }
    return referenceSet
  }
}