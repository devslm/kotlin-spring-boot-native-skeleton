package com.app.service.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.slmdev.jsonapi.simple.response.Meta
import com.slmdev.jsonapi.simple.response.Response
import io.github.oshai.KotlinLogging
import io.vavr.control.Try
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class WebSocketService(
    private val objectMapper: ObjectMapper,
): TextWebSocketHandler() {
    protected val logger = KotlinLogging.logger {}

    private val webSocketSessions: ConcurrentHashMap<String, ConcurrentWebSocketSessionDecorator> = ConcurrentHashMap()

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.info { "Abnormal close websocket connection with URI: ${getUriPath(session)}! Reason: ${exception.message}" }

        webSocketSessions.remove(session.id)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info { "Accept new websocket connection with URI: ${getUriPath(session)}" }

        webSocketSessions[session.id] = ConcurrentWebSocketSessionDecorator(session, 5_000, 8192)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info { "Close websocket connection with URI: ${getUriPath(session)} and status: $status" }

        webSocketSessions.remove(session.id)
    }

    private fun getUriPath(session: WebSocketSession): String {
        return session.uri?.path ?: "undefined"
    }

    protected fun send(message: Any) {
        webSocketSessions.forEach { (_, webSocketSession) ->
            Try.of {
                TextMessage(
                    objectMapper.writeValueAsString(
                        Response.builder<Any, Any>()
                            .jsonApiType("messages")
                            .data(message)
                            .metaWebSocket(Meta.WebSocket(UUID.randomUUID()))
                            .build()
                    )
                )
            }.onFailure { exception  -> logger.error {
                "Could not send message: $message via websocket to connected client! " +
                    "Error while message converting to string: ${exception.message}"
            }}.peek { message -> webSocketSession.sendMessage(message) }
        }
    }
}
