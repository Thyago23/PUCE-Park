package ec.edu.puce.park.mapper

import ec.edu.puce.park.dto.PerfilUsuarioResponseDTO
import ec.edu.puce.park.dto.PuestoParqueoResponseDTO
import ec.edu.puce.park.dto.ZonaParqueoResponseDTO
import ec.edu.puce.park.entity.PerfilUsuario
import ec.edu.puce.park.entity.PuestoParqueo
import ec.edu.puce.park.entity.ZonaParqueo
import org.springframework.stereotype.Component

@Component
class ZonaParqueoMapper {
    fun toDto(entity: ZonaParqueo) = ZonaParqueoResponseDTO(
        id = entity.id,
        nombre = entity.nombre,
        descripcion = entity.descripcion
    )
}

@Component
class PuestoParqueoMapper {
    fun toDto(entity: PuestoParqueo) = PuestoParqueoResponseDTO(
        id = entity.id,
        zonaId = entity.zona.id,
        numeroPuesto = entity.numeroPuesto,
        estado = entity.estado.name
    )
}

@Component
class PerfilUsuarioMapper {
    fun toDto(entity: PerfilUsuario) = PerfilUsuarioResponseDTO(
        id = entity.id,
        username = entity.username,
        nombreCompleto = entity.nombreCompleto,
        placaVehiculo = entity.placaVehiculo,
        modoOscuro = entity.modoOscuro
    )
}
