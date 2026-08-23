package com.panificadora.isra.ptvisr.controllers

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.ui.Model

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceededException(
        ex: MaxUploadSizeExceededException,
        request: WebRequest
    ): ResponseEntity<Map<String, String>> {
        logger.warn("Upload size exceeded [${request.getDescription(false)}]: ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
            mapOf("error" to "El archivo es muy grande. El tamaño máximo permitido es 10 MB.")
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): String {
        logger.warn("Bad Request [${request.getDescription(false)}]: ${ex.message}", ex)
        return "layout :: mainPage(page='dashboard', fragment='content')"
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): String {
        logger.error("Internal Server Error [${request.getDescription(false)}]: ${ex.message}", ex)
        return "layout :: mainPage(page='dashboard', fragment='content')"
    }
}
