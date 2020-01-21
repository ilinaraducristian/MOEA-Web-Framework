package com.ilinaraducristian.moeawebframework.moea

import org.moeaframework.core.NondominatedPopulation
import org.moeaframework.core.PopulationIO
import org.moeaframework.core.Problem
import org.moeaframework.core.spi.ProblemProvider
import java.io.File
import java.net.MalformedURLException
import java.net.URLClassLoader

class CustomProblemProvider: ProblemProvider() {

  override fun getProblem(name: String): Problem? {
    val file = File("problems/$name.class")
    var problem: Problem? = null
    if(file.exists())
      try {
        problem = URLClassLoader(arrayOf(file.toURI().toURL())).loadClass(name).newInstance() as Problem
      }catch(e: MalformedURLException){
        println("MalformedURLException")
      }catch(e: ClassNotFoundException) {
        println("ClassNotFoundException")
      }
    return problem;
  }

  override fun getReferenceSet(name: String): NondominatedPopulation? {
    val file = File("references/$name.pf")
    if(file.exists())
      return NondominatedPopulation(PopulationIO.readObjectives(file))
    else return null
  }
}