package com.app.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableScheduling
@EnableRetry
@EnableTransactionManagement
@EntityScan(basePackages = ["com.app.entity"])
@EnableJpaRepositories(basePackages = ["com.app.repository"])
@ComponentScan("com.app")
class AppConfig
