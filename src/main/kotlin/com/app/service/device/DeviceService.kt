package com.app.service.device

import com.app.dto.device.DeviceDto
import com.app.mapper.device.DeviceMapper
import com.app.repository.device.DeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

@Service
class DeviceService(
    private val deviceMapper: DeviceMapper,
    private val deviceRepository: DeviceRepository,
) {
    private val coroutineScope = CoroutineScope(
        Executors.newFixedThreadPool(2)
            .asCoroutineDispatcher()
    )

    fun getAll(): List<DeviceDto> = deviceMapper.toDto(deviceRepository.findAll())

    fun getFirstActive(onSuccess: (device: DeviceDto) -> Unit) {
        // Just for demonstration
        coroutineScope.launch {
            while (true) {
                // Simulate long-running job
                delay(20_000L)

                val devices = getAll()

                onSuccess(devices.random())
            }
        }
    }
}
