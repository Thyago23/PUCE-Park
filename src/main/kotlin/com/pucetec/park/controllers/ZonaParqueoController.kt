package com.pucetec.park.controllers

import com.pucetec.park.dto.CreateZonaParqueoRequest
import com.pucetec.park.dto.UpdateZonaParqueoRequest
import com.pucetec.park.dto.ZonaParqueoResponse
import com.pucetec.park.services.ZonaParqueoService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/zonas")
class ZonaParqueoController(
    private val zonaParqueoService: ZonaParqueoService
) {
    private val logger = LoggerFactory.getLogger(ZonaParqueoController::class.java)

    @GetMapping
    fun getAllZonas(): List<ZonaParqueoResponse> {
        logger.info("GET /api/v1/zonas")
        return zonaParqueoService.getAllZonas()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createZona(@RequestBody request: CreateZonaParqueoRequest): ZonaParqueoResponse {
        logger.info("POST /api/v1/zonas - nombre=${request.nombre}")
        return zonaParqueoService.createZona(request)
    }

    @PutMapping("/{id}")
    fun updateZona(@PathVariable id: Long, @RequestBody request: UpdateZonaParqueoRequest): ZonaParqueoResponse {
        logger.info("PUT /api/v1/zonas/$id")
        return zonaParqueoService.updateZona(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteZona(@PathVariable id: Long) {
        logger.info("DELETE /api/v1/zonas/$id")
        zonaParqueoService.deleteZona(id)
    }
}
