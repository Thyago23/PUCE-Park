package com.pucetec.park.entities

import jakarta.persistence.*

@Entity
@Table(
    name = "puestos_parqueo",
    uniqueConstraints = [UniqueConstraint(columnNames = ["zona_id", "numero_puesto"])]
)
class PuestoParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id", nullable = false)
    val zona: ZonaParqueo? = null,

    @Column(name = "numero_puesto", length = 10, nullable = false)
    var numeroPuesto: String = "",

    @Column(name = "fila", length = 30, nullable = false)
    var fila: String = "",

    @Column(name = "orden", nullable = false)
    var orden: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    var estado: EstadoPuesto = EstadoPuesto.DISPONIBLE
)
