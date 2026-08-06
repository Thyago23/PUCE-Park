package com.pucetec.users.repositories

import com.pucetec.users.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserProfileRepository : JpaRepository<UserProfile, Long> {
    fun findBySub(sub: String): Optional<UserProfile>
    fun findByUsername(username: String): Optional<UserProfile>
    fun existsBySub(sub: String): Boolean
}
