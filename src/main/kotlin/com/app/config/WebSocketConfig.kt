package com.app.config

import com.app.service.websocket.AppStatusWebSocketService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig: WebSocketConfigurer {
    @Autowired
    private lateinit var appStatusWebSocketService: AppStatusWebSocketService

    override fun registerWebSocketHandlers(
        registry: WebSocketHandlerRegistry
    ) {
        registry.addHandler(
            appStatusWebSocketService,
            AppStatusWebSocketService.URI
        ).setAllowedOrigins("*")
    }
}
