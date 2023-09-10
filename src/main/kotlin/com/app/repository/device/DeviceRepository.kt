package com.app.repository.device

import com.app.entity.device.DeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DeviceRepository: JpaRepository<DeviceEntity, UUID>
