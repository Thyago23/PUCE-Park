package com.pucetec.park.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "historial_parqueo")
class HistorialParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puesto_id", nullable = false)
    val puesto: PuestoParqueo? = null,

    @Column(name = "username", length = 60, nullable = false)
    val username: String = "",

    @Column(name = "codigo_ticket", length = 15, nullable = false, unique = true)
    val codigoTicket: String = "",

    @Column(name = "fecha_ingreso", nullable = false)
    val fechaIngreso: LocalDateTime = LocalDateTime.now(),

    @Column(name = "fecha_salida")
    var fechaSalida: LocalDateTime? = null,

    @Column(name = "placa_vehiculo", length = 15)
    val placaVehiculo: String? = null
)
