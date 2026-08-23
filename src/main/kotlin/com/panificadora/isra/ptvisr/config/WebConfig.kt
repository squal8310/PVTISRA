package com.panificadora.isra.ptvisr.config

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Configuration

@Configuration
class WebConfig : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    override fun customize(factory: TomcatServletWebServerFactory) {
        factory.addConnectorCustomizers { connector ->
            // Increase the maximum file upload size limit
            connector.setProperty("maxHttpFormPostSize", "12582912") // 12MB in bytes
            connector.setProperty("maxConnections", "200")
        }
    }
}
