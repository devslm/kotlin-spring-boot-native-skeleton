package com.app.entity.device

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Entity
@Table(name = "devices")
data class DeviceEntity(
    @Id
    var id: UUID,
    var name: String,
    var dateAdd: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
)
