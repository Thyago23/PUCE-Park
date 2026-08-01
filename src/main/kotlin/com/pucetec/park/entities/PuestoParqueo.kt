package com.pucetec.park.entities

import jakarta.persistence.*

@Entity
@Table(
    name = "parking_spaces",
    uniqueConstraints = [UniqueConstraint(columnNames = ["zone_id", "space_number"])]
)
class PuestoParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    val zona: ZonaParqueo? = null,

    @Column(name = "space_number", length = 10, nullable = false)
    var numeroPuesto: String = "",

    @Column(name = "row", length = 30, nullable = false)
    var fila: String = "",

    @Column(name = "order_index", nullable = false)
    var orden: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    var estado: EstadoPuesto = EstadoPuesto.DISPONIBLE
)
