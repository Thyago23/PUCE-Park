package ec.edu.puce.park.controller

import ec.edu.puce.park.dto.ZonaParqueoRequestDTO
import ec.edu.puce.park.dto.ZonaParqueoResponseDTO
import ec.edu.puce.park.service.ZonaParqueoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/zonas")
class ZonaParqueoController(
    private val zonaParqueoService: ZonaParqueoService
) {

    @GetMapping
    fun getAllZonas(): List<ZonaParqueoResponseDTO> {
        return zonaParqueoService.getAllZonas()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createZona(@Valid @RequestBody request: ZonaParqueoRequestDTO): ZonaParqueoResponseDTO {
        return zonaParqueoService.createZona(request)
    }

    @PutMapping("/{id}")
    fun updateZona(@PathVariable id: Long, @Valid @RequestBody request: ZonaParqueoRequestDTO): ZonaParqueoResponseDTO {
        return zonaParqueoService.updateZona(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteZona(@PathVariable id: Long) {
        zonaParqueoService.deleteZona(id)
    }
}
