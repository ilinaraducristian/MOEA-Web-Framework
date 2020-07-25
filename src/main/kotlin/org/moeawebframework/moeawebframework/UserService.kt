package org.moeawebframework.moeawebframework

import org.moeawebframework.moeawebframework.entities.UserDao
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userDao: UserDao
) {



}