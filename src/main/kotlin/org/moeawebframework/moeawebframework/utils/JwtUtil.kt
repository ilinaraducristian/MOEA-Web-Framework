package org.moeawebframework.moeawebframework.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.nio.charset.Charset
import java.security.Key
import java.util.*

  val key: Key = Keys.hmacShaKeyFor("x\u0001�\u0006�2[u�U||g�> V\u000FiV���O\u001B��b�X".toByteArray())

fun createToken(): String {
  return Jwts.builder().setSubject("username").setIssuedAt(Date()).setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)).signWith(key).compact()
}

fun validateJwt(jwt: String): Jws<Claims> {
  return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt)
}