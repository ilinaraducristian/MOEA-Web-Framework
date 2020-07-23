package com.ilinaraducristian.moeawebframework

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import java.util.function.Function
import kotlin.collections.HashMap

@Value("\${jwt.token}")
private val token = "SkdIMUtCM3RaT3YmTW01cDk0QiM2QlVpXk4zdnY0NDk="

//@Service
//class JwtUtil

fun extractUsername(token: String): String {
  return extractClaim(token, Function { obj: Claims -> obj.subject })
}

fun extractExpiration(token: String): Date {
  return extractClaim(token, Function { obj: Claims -> obj.expiration })
}

fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
  val claims = extractAllClaims(token)
  return claimsResolver.apply(claims)
}

private fun extractAllClaims(token: String): Claims {
  return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(token.toByteArray())).parseClaimsJws(token).body
}

private fun isTokenExpired(token: String): Boolean {
  return extractExpiration(token).before(Date())
}

fun generateToken(userDetails: UserDetails): String {
  val claims: Map<String, Any> = HashMap()
  return createToken(claims, userDetails.username)
}

private fun createToken(claims: Map<String, Any>, subject: String): String {
  return Jwts.builder().setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
      .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
      .signWith(Keys.hmacShaKeyFor(token.toByteArray())).compact()
}

fun validateToken(token: String, userDetails: UserDetails): Boolean {
  val username = extractUsername(token)
  return username == userDetails.username && !isTokenExpired(token)
}
