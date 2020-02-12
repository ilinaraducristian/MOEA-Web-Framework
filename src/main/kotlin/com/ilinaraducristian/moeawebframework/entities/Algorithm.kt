package com.ilinaraducristian.moeawebframework.entities

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Algorithm(

    @Id
    @GeneratedValue
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    @NotBlank
    var name: String = "",

    @Column(nullable = false, unique = true)
    @NotBlank
    var filePath: String = "",

    @ManyToMany(mappedBy = "algorithms")
    var users: MutableSet<User> = HashSet(),

    @OneToMany(mappedBy = "algorithm")
    var queue: MutableList<QueueItem> = ArrayList()

)