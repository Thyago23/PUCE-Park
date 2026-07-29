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

    @ExceptionHandler(HistorialParqueoNotFoundException::class)
    fun handleHistorialParqueoNotFound(e: HistorialParqueoNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(e.message ?: "Historial de parqueo no encontrado", "PuestoParqueoService"))

    @ExceptionHandler(SlotAlreadyOccupiedException::class)
    fun handleSlotAlreadyOccupied(e: SlotAlreadyOccupiedException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(e.message ?: "El puesto ya está ocupado", "PuestoParqueoService"))

    @ExceptionHandler(SlotAlreadyAvailableException::class)
    fun handleSlotAlreadyAvailable(e: SlotAlreadyAvailableException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(e.message ?: "El puesto ya está disponible", "PuestoParqueoService"))

    @ExceptionHandler(UnauthorizedAccessException::class)
    fun handleUnauthorizedAccess(e: UnauthorizedAccessException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ExceptionResponse(e.message ?: "Acceso no autorizado", "PuestoParqueoService"))

    @ExceptionHandler(ZonaParqueoNombreDuplicadoException::class)
    fun handleZonaParqueoNombreDuplicado(e: ZonaParqueoNombreDuplicadoException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(e.message ?: "Ya existe una zona con ese nombre", "ZonaParqueoService"))

    @ExceptionHandler(NumeroPuestoDuplicadoException::class)
    fun handleNumeroPuestoDuplicado(e: NumeroPuestoDuplicadoException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(e.message ?: "Ya existe ese número de puesto en la zona", "PuestoParqueoService"))

    @ExceptionHandler(ZonaParqueoLlenaException::class)
    fun handleZonaParqueoLlena(e: ZonaParqueoLlenaException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(e.message ?: "La zona de parqueo está llena", "PuestoParqueoService"))

    @ExceptionHandler(ZonaConPuestosException::class)
    fun handleZonaConPuestos(e: ZonaConPuestosException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(e.message ?: "La zona tiene puestos asignados", "ZonaParqueoService"))

    @ExceptionHandler(BlankFieldException::class)
    fun handleBlankField(e: BlankFieldException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(e.message ?: "Campo requerido no puede estar vacío", "ValidationService"))

    @ExceptionHandler(InvalidCapacityException::class)
    fun handleInvalidCapacity(e: InvalidCapacityException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(e.message ?: "Capacidad inválida", "ZonaParqueoService"))
}
