package com.app.dto.device

import com.slmdev.jsonapi.simple.annotation.JsonApiId
import com.slmdev.jsonapi.simple.annotation.JsonApiType
import java.time.LocalDateTime
import java.util.*

@JsonApiType("devices")
data class DeviceDto(
    @JsonApiId
    val id: UUID,
    val name: String,
    val dateAdd: LocalDateTime,
)
