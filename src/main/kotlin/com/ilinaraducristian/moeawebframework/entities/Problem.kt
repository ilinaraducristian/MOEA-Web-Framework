package com.ilinaraducristian.moeawebframework.entities

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Problem(

    @Id
    @GeneratedValue
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    @NotBlank
    var name: String = "",

    @Column(nullable = false, unique = true)
    @NotBlank
    var filePath: String = "",

    @ManyToMany(mappedBy = "problems")
    var users: MutableSet<User> = HashSet(),

    @OneToMany(mappedBy = "problem")
    var queue: MutableList<QueueItem> = ArrayList()

)