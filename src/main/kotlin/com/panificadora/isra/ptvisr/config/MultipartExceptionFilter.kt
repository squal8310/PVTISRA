package com.panificadora.isra.ptvisr.config

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory

@Component
class MultipartExceptionFilter : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(MultipartExceptionFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            val isUploadSizeError = e.message?.contains("Maximum upload size exceeded", ignoreCase = true) == true ||
                e.cause?.message?.contains("Maximum upload size exceeded", ignoreCase = true) == true ||
                e.message?.contains("FileCountLimitExceededException", ignoreCase = true) == true ||
                e.cause?.message?.contains("FileCountLimitExceededException", ignoreCase = true) == true

            if (isUploadSizeError) {
                logger.warn("Upload/multipart error [${request.method} ${request.requestURI}]: ${e.message}", e)

                response.status = HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE
                response.contentType = "application/json; charset=UTF-8"
                response.writer.write("""{"error":"La solicitud multipart excede los límites permitidos. Intenta con menos campos o un archivo más pequeño."}""")
                response.writer.flush()
                return
            }
            throw e
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val isMultipart = request.contentType?.contains("multipart/form-data", ignoreCase = true) ?: false
        return !isMultipart
    }
}
