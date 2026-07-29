package ec.edu.puce.park.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.message ?: "Not Found"), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SlotAlreadyOccupiedException::class)
    fun handleSlotAlreadyOccupied(ex: SlotAlreadyOccupiedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(HttpStatus.CONFLICT.value(), ex.message ?: "Conflict"), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(UnauthorizedAccessException::class)
    fun handleUnauthorizedAccess(ex: UnauthorizedAccessException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.message ?: "Forbidden"), HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity(ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors), HttpStatus.BAD_REQUEST)
    }
}

data class ErrorResponse(
    val status: Int,
    val message: String
)
