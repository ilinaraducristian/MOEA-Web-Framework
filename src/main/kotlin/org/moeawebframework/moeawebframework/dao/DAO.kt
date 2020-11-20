package org.moeawebframework.moeawebframework.dao

interface DAO<T> {

  suspend fun get(id: Any): T?

  suspend fun getAll(): List<T>

  suspend fun save(t: T): T?

  suspend fun update(t: T, fields: HashMap<String, Any?>)

  suspend fun delete(t: T)

}