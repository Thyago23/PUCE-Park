package com.pucetec.park.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ExceptionResponse(val message: String, val source: String)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ZonaParqueoNotFoundException::class)
    fun handleZonaParqueoNotFound(e: ZonaParqueoNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(e.message ?: "Zona de parqueo no encontrada", "ZonaParqueoService"))

    @ExceptionHandler(PuestoParqueoNotFoundException::class)
    fun handlePuestoParqueoNotFound(e: PuestoParqueoNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(e.message ?: "Puesto de parqueo no encontrado", "PuestoParqueoService"))

    @ExceptionHandler(SlotAlreadyOccupiedException::class)
    fun handleSlotAlreadyOccupied(e: SlotAlreadyOccupiedException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(e.message ?: "El puesto ya está ocupado", "PuestoParqueoService"))

    @ExceptionHandler(UnauthorizedAccessException::class)
    fun handleUnauthorizedAccess(e: UnauthorizedAccessException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ExceptionResponse(e.message ?: "No autorizado", "PuestoParqueoService"))

    @ExceptionHandler(HistorialParqueoNotFoundException::class)
    fun handleHistorialParqueoNotFound(e: HistorialParqueoNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(e.message ?: "Historial no encontrado", "PuestoParqueoService"))
}
