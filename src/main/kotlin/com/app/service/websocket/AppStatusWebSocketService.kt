package com.app.service.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class AppStatusWebSocketService(
    val objectMapper: ObjectMapper,
): WebSocketService(objectMapper) {
    companion object {
        const val URI = "/ws/status"
    }

    @Scheduled(fixedDelay = 15_000L, initialDelay = 3_000L)
    fun sendStatus() = send(Status("OK"))

    private data class Status(
        val status: String,
    )
}
