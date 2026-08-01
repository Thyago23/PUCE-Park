package com.pucetec.park.entities

import jakarta.persistence.*

@Entity
@Table(
    name = "parking_zones",
    uniqueConstraints = [UniqueConstraint(columnNames = ["name"])]
)
class ZonaParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "name", length = 50, nullable = false, unique = true)
    var nombre: String = "",

    @Column(name = "description", length = 255)
    var descripcion: String? = null,

    @Column(name = "max_capacity", nullable = false)
    var capacidadMaxima: Int = 0,

    @Column(name = "location", length = 255)
    var ubicacion: String? = null,
)
