package com.app.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.support.RetryTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class BeanConfig {
    companion object {
        fun newObjectMapper(includeNull: Boolean = false): ObjectMapper {
            val jacksonMapperBuilder = jacksonMapperBuilder()

            jacksonMapperBuilder.addModule(JavaTimeModule())
            jacksonMapperBuilder.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            jacksonMapperBuilder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            jacksonMapperBuilder.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)

            if (!includeNull) {
                jacksonMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL)
            }
            return jacksonMapperBuilder.build()
        }
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("*")
            }
        }
    }

    @Bean
    fun taskScheduler(): ThreadPoolTaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = 32

        return threadPoolTaskScheduler
    }

    @Bean
    fun objectMapper() = newObjectMapper()

    @Bean
    fun retryTemplate(): RetryTemplate = RetryTemplate.builder()
        .maxAttempts(4)
        .fixedBackoff(500L)
        .retryOn(Exception::class.java)
        .build()

    @Bean
    fun restTemplate() = RestTemplate()

    @Bean
    fun okHttpClient() = OkHttpClient()
}
