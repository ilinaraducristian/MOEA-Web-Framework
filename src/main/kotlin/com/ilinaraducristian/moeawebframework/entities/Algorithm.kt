package com.ilinaraducristian.moeawebframework.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Algorithm(

    @Id
    @GeneratedValue
    @JsonIgnore
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    @NotBlank
    var name: String = "",

    @ManyToMany(mappedBy = "algorithms")
    @JsonIgnore
    var users: MutableSet<User> = HashSet(),

    @OneToMany(mappedBy = "algorithm")
    @JsonIgnore
    var queue: MutableList<QueueItem> = ArrayList()

): Serializable {
    override fun toString(): String {
        return """{"name"="$name"}"""
    }
}