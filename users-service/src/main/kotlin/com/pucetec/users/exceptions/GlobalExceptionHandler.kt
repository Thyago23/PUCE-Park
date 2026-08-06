package com.pucetec.users.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ExceptionResponse(val message: String, val source: String)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserProfileNotFoundException::class)
    fun handleUserProfileNotFound(e: UserProfileNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(e.message ?: "User profile not found", "UserProfileService"))

    @ExceptionHandler(UserProfileAlreadyExistsException::class)
    fun handleUserProfileAlreadyExists(e: UserProfileAlreadyExistsException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(e.message ?: "User profile already exists", "UserProfileService"))
}
