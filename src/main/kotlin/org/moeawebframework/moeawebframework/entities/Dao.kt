package org.moeawebframework.moeawebframework.entities

import kotlinx.coroutines.flow.Flow

interface Dao<T> {

  suspend fun get(id: Long): T

  suspend fun getAll(): Flow<T>

  suspend fun save(t: T): T

  suspend fun delete(t: T)

}