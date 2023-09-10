package com.app.controller

import com.app.dto.device.DeviceDto
import com.app.service.device.DeviceService
import com.slmdev.jsonapi.simple.response.Data
import com.slmdev.jsonapi.simple.response.Response
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/devices"], produces = [MediaType.APPLICATION_JSON_VALUE])
class DeviceController(
    private val deviceService: DeviceService,
) {
    @GetMapping
    fun getAll(): Response<List<Data<DeviceDto>>> {
        return Response.ResponseBuilder<List<Data<DeviceDto>>, DeviceDto>()
            .data(deviceService.getAll())
            .build()
    }
}
