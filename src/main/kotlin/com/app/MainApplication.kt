package com.app

import io.github.oshai.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.runApplication
import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootApplication
class MainApplication(
    private val buildProperties: BuildProperties,
) {
    private val logger = KotlinLogging.logger {}

    init {
        logger.info { "App version: ${buildProperties.version}" }
        logger.info { "App build time: ${LocalDate.ofInstant(buildProperties.time, ZoneOffset.UTC)}" }
    }
}

fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
}
