package com.pucetec.park.entities

import jakarta.persistence.*

@Entity
@Table(name = "zonas_parqueo")
class ZonaParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "nombre", length = 50, nullable = false)
    var nombre: String = "",

    @Column(name = "descripcion", length = 255)
    var descripcion: String = "",
)
