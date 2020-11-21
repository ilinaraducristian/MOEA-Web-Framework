package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserDAO(
    private val userRepository: UserRepository
) : DAO<User, String>(userRepository) {

}