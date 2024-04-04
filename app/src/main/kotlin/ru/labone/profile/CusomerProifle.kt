package ru.labone.profile

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URI
import java.time.LocalDate


@Entity
data class CustomerProfile(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String? = null,
    val surname: String? = null,
    val gender: Gender? = null,
    val birthDate: LocalDate? = null,
    val customerAvatar: URI? = null
)


enum class Gender {
    MALE, FEMALE
}