package com.pucetec.park.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "parking_history")
class HistorialParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    val puesto: PuestoParqueo? = null,

    @Column(name = "username", length = 60, nullable = false)
    val username: String = "",

    @Column(name = "ticket_code", length = 15, nullable = false, unique = true)
    val codigoTicket: String = "",

    @Column(name = "entry_date", nullable = false)
    val fechaIngreso: LocalDateTime = LocalDateTime.now(),

    @Column(name = "exit_date")
    var fechaSalida: LocalDateTime? = null,

    @Column(name = "vehicle_plate", length = 15)
    val placaVehiculo: String? = null,

    // Nombre para mostrar (denormalizado desde users-service al ocupar) — para el ranking
    @Column(name = "display_name", length = 100)
    val nombreMostrar: String? = null
)
