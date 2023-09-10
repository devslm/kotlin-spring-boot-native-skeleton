package com.app.service.test

import com.app.entity.device.DeviceEntity
import com.app.repository.device.DeviceRepository
import com.app.service.websocket.AppStatusWebSocketService
import io.github.oshai.KotlinLogging
import jakarta.annotation.PostConstruct
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Service
class TestService(
    private val deviceRepository: DeviceRepository,
    private val okHttpClient: OkHttpClient,
    @Value("\${server.port}")
    private val serverPort: Int,
    @Value("\${spring.mvc.servlet.path}")
    private val servletPath: String,
) {
    private val logger = KotlinLogging.logger {}

    private val serverHost = "http://127.0.0.1:$serverPort$servletPath"
    private val websocketServerHost = "ws://127.0.0.1:$serverPort$servletPath${AppStatusWebSocketService.URI}"

    protected var webSocket: WebSocket? = null

    @PostConstruct
    fun addTestData() {
        if (deviceRepository.findAll().isNotEmpty()) {
            return
        }
        deviceRepository.saveAll(
            listOf(
                DeviceEntity(id = UUID.randomUUID(), name = "device 1"),
                DeviceEntity(id = UUID.randomUUID(), name = "device 2"),
                DeviceEntity(id = UUID.randomUUID(), name = "device 3"),
            )
        )
    }

    @Scheduled(fixedDelay = 10_000L, initialDelay = 3_000L)
    fun showDevicesPeriodically() {
        val request = Request.Builder()
            .url("$serverHost/devices")
            .build()

        val response = okHttpClient.newCall(request)
            .execute()

        logger.info { "Devices: ${response.body?.string()}" }
    }

    @Scheduled(fixedDelay = 5_000L, initialDelay = 3_000L)
    fun reConnectToWebsocket() {
        if (webSocket != null) {
            return
        }
        subscribeToWebSocket(
            uri = websocketServerHost,
            callback = { logger.info { "New websocket app status response: $it" } },
        )
    }

    private fun subscribeToWebSocket(uri: String, callback: (response: String) -> Unit) {
        val request = Request.Builder()
            .url(uri)
            .build()

        this.webSocket = OkHttpClient().newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    callback(text)
                } catch (exception: Exception) {
                    logger.error(exception) { exception.message }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                logger.error { "Could not create new websocket connection with uri: $uri! Reason: ${t.message}" }
            }
        })
    }
}
