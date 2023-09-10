package com.app.service.device

import com.app.dto.device.DeviceDto
import com.app.mapper.device.DeviceMapper
import com.app.repository.device.DeviceRepository
import org.springframework.stereotype.Service

@Service
class DeviceService(
    private val deviceMapper: DeviceMapper,
    private val deviceRepository: DeviceRepository,
) {
    fun getAll(): List<DeviceDto> = deviceMapper.toDto(deviceRepository.findAll())
}
