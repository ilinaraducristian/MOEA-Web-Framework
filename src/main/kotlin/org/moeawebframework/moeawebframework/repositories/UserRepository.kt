package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ReactiveCrudRepository<User, Int> {

}