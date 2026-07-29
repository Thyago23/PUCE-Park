package ec.edu.puce.park.controller

import ec.edu.puce.park.dto.PuestoParqueoRequestDTO
import ec.edu.puce.park.dto.PuestoParqueoResponseDTO
import ec.edu.puce.park.service.PuestoParqueoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/puestos")
class PuestoParqueoController(
    private val puestoParqueoService: PuestoParqueoService
) {

    @GetMapping("/zona/{zonaId}")
    fun getPuestosByZona(@PathVariable zonaId: Long): List<PuestoParqueoResponseDTO> {
        return puestoParqueoService.getPuestosByZona(zonaId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPuesto(@Valid @RequestBody request: PuestoParqueoRequestDTO): PuestoParqueoResponseDTO {
        return puestoParqueoService.createPuesto(request)
    }

    @PutMapping("/{id}")
    fun updatePuesto(@PathVariable id: Long, @Valid @RequestBody request: PuestoParqueoRequestDTO): PuestoParqueoResponseDTO {
        return puestoParqueoService.updatePuesto(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePuesto(@PathVariable id: Long) {
        puestoParqueoService.deletePuesto(id)
    }

    @PostMapping("/{id}/ocupar")
    fun ocuparPuesto(@PathVariable id: Long, @AuthenticationPrincipal jwt: Jwt): PuestoParqueoResponseDTO {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        return puestoParqueoService.ocuparPuesto(id, username)
    }

    @PostMapping("/{id}/liberar")
    fun liberarPuesto(@PathVariable id: Long, @AuthenticationPrincipal jwt: Jwt): PuestoParqueoResponseDTO {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        return puestoParqueoService.liberarPuesto(id, username, isGuard = false)
    }

    @PatchMapping("/{id}/forzar-liberacion")
    fun forzarLiberacion(@PathVariable id: Long, @AuthenticationPrincipal jwt: Jwt): PuestoParqueoResponseDTO {
        val username = jwt.getClaimAsString("username") ?: jwt.subject
        return puestoParqueoService.liberarPuesto(id, username, isGuard = true)
    }
}
