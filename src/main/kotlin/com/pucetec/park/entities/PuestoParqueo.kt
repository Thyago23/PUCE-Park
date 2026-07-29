package com.pucetec.park.entities

import jakarta.persistence.*

@Entity
@Table(name = "puestos_parqueo")
class PuestoParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id", nullable = false)
    val zona: ZonaParqueo? = null,

    @Column(name = "numero_puesto", length = 10, nullable = false)
    var numeroPuesto: String = "",

    @Column(name = "estado", length = 20, nullable = false)
    var estado: String = "DISPONIBLE"
)
