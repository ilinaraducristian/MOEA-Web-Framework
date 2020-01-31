package com.ilinaraducristian.moeawebframework.dto

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "authorities")
data class Authority(

    @Id
    @GeneratedValue
    var id: Long = 0,

    var authority: String = "USER",

    @ManyToOne
    @JoinColumn(name = "username", nullable = false, referencedColumnName = "username")
    var user: User = User()

) : Serializable