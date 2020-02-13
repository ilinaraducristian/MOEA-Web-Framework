package com.ilinaraducristian.moeawebframework.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.HashSet
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

    @ManyToMany(mappedBy = "problems", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JsonIgnore
    var users: MutableSet<User> = HashSet(),

    @OneToMany(mappedBy = "problem")
    @JsonIgnore
    var queue: MutableSet<QueueItem> = HashSet()

): Serializable {
    override fun toString(): String {
        return "Problem(id=$id, name='$name')"
    }
}