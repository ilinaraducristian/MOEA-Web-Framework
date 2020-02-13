package com.ilinaraducristian.moeawebframework.moea

import com.ilinaraducristian.moeawebframework.security.UserPrincipal
import org.moeaframework.core.Algorithm
import org.moeaframework.core.Problem
import org.moeaframework.core.spi.AlgorithmProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.io.File
import java.net.MalformedURLException
import java.net.URLClassLoader
import java.util.*

@Component
class CustomAlgorithmProvider : AlgorithmProvider() {

  @Suppress("DEPRECATION")
  override fun getAlgorithm(name: String?, properties: Properties?, problem: Problem?): Algorithm? {
    var algorithm: Algorithm? = null
    var algorithms = File("moeaData/public/algorithms").list()?.find { algorithm ->
      algorithm == "$name.class"
    }
    if (algorithms != null) {
      val file = File("moeaData/public/algorithms/$name.class")
      if (file.exists())
        try {
          algorithm = URLClassLoader(arrayOf(file.toURI().toURL())).loadClass(name).newInstance() as Algorithm
        } catch (e: MalformedURLException) {
          println("MalformedURLException")
        } catch (e: ClassNotFoundException) {
          println("ClassNotFoundException")
        }
      return algorithm
    }
    val userName = (SecurityContextHolder.getContext().authentication.principal as UserPrincipal).username
    algorithms = File("moeaData/users/$userName/algorithms").list()?.find { algorithm ->
      algorithm == "$name.class"
    }
    if (algorithms != null) {
      val file = File("moeaData/users/$userName/algorithms/$name.class")
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