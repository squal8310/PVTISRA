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
            if (e.message?.contains("Maximum upload size exceeded", ignoreCase = true) == true ||
                e.cause?.message?.contains("Maximum upload size exceeded", ignoreCase = true) == true) {
                logger.warn("Upload size exceeded: ${e.message}", e)

                // Set response as JSON with 413 status
                response.status = HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE
                response.contentType = "application/json; charset=UTF-8"
                response.writer.write("""{"error":"El archivo es muy grande. El tamaño máximo permitido es 10 MB."}""")
                response.writer.flush()
                return
            }
            throw e
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        // Only apply to multipart requests
        return !request.contentType?.contains("multipart/form-data", ignoreCase = true) ?: true
    }
}
