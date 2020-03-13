package com.ilinaraducristian.moeawebframework.moea

import org.moeaframework.core.Algorithm
import org.moeaframework.core.Problem
import org.moeaframework.core.spi.AlgorithmProvider
import org.springframework.stereotype.Component
import java.io.File
import java.net.MalformedURLException
import java.net.URLClassLoader
import java.util.*

class CustomAlgorithmProvider(
    private val username: String
) : AlgorithmProvider() {

  @Suppress("DEPRECATION")
  override fun getAlgorithm(name: String?, properties: Properties?, problem: Problem?): Algorithm? {
    var algorithm: Algorithm? = null
    var algorithms = File("moeaData/$username/algorithms").list()?.find { algorithm ->
      algorithm == "$name.class"
    }
    if (algorithms != null) {
      val file = File("moeaData/$username/algorithms/$name.class")
      if (file.exists())
        try {
          algorithm = URLClassLoader(arrayOf(file.toURI().toURL())).loadClass(name).newInstance() as Algorithm
        } catch (e: MalformedURLException) {
          println("MalformedURLException")
        } catch (e: ClassNotFoundException) {
          println("ClassNotFoundException")
        }
    }
    return algorithm
  }

}