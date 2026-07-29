package ec.edu.puce.park.dto

import jakarta.validation.constraints.NotBlank

data class ZonaParqueoRequestDTO(
    @field:NotBlank(message = "El nombre es obligatorio")
    val nombre: String,
    val descripcion: String? = null
)

data class ZonaParqueoResponseDTO(
    val id: Long,
    val nombre: String,
    val descripcion: String?
)

data class PuestoParqueoRequestDTO(
    val zonaId: Long,
    @field:NotBlank(message = "El numero de puesto es obligatorio")
    val numeroPuesto: String
)

data class PuestoParqueoResponseDTO(
    val id: Long,
    val zonaId: Long,
    val numeroPuesto: String,
    val estado: String
)

data class PerfilUsuarioRequestDTO(
    val nombreCompleto: String? = null,
    val placaVehiculo: String? = null,
    val modoOscuro: Boolean = false
)

data class PerfilUsuarioResponseDTO(
    val id: Long,
    val username: String,
    val nombreCompleto: String?,
    val placaVehiculo: String?,
    val modoOscuro: Boolean
)
