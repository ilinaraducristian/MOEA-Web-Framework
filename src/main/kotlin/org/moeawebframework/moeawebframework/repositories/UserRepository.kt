package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.User
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : R2dbcRepository<User, Long>