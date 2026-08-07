package com.pucetec.users.entities

import jakarta.persistence.*

@Entity
@Table(name = "user_profiles")
class UserProfile(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "sub", length = 64, nullable = false, unique = true)
    val sub: String = "",

    @Column(name = "username", length = 60, nullable = false, unique = true)
    val username: String = "",

    @Column(name = "full_name", length = 100)
    var fullName: String = "",

    @Column(name = "vehicle_plate", length = 15)
    var vehiclePlate: String = "",

    @Column(name = "permit_number", length = 20, unique = true, nullable = true)
    var permitNumber: String? = null,

    @Column(name = "dark_mode", nullable = false)
    var darkMode: Boolean = false,
)
