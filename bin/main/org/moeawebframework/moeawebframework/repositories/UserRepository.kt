package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.User
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface UserRepository : R2dbcRepository<User, String>