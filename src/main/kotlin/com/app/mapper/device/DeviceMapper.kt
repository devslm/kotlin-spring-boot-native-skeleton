package com.app.mapper.device

import com.app.dto.device.DeviceDto
import com.app.entity.device.DeviceEntity
import org.mapstruct.IterableMapping
import org.mapstruct.Mapper
import org.mapstruct.Named
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Mapper(
    imports = [
        UUID::class,
        LocalDateTime::class,
        ZoneOffset::class,
    ]
)
interface DeviceMapper {
    @IterableMapping(qualifiedByName = ["toDto"])
    fun toDto(deviceEntities: List<DeviceEntity>): List<DeviceDto>

    @Named("toDto")
    fun toDto(deviceEntity: DeviceEntity): DeviceDto
}
