import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jdkVersion = JavaVersion.VERSION_17
val jacksonVersion = "2.14.0"
val jacksonDatabindVersion = "2.14.0"
val jacksonDataFormatVersion = "2.14.0"
val vavrVersion = "0.10.2"
val sqliteVersion = "3.43.0.0"
val hibernateCommunityDialectsVersion = "6.1.6.Final"
val mapstructVersion = "1.5.3.Final"
val caffeineVersion = "3.1.6"
val kotlinLoggingVersion = "4.0.0-beta-22"
val kotestVersion = "5.6.2"
val junitPioneerVersion = "2.0.1"
val springmockkVersion = "4.0.2"
val mockkVersion = "1.13.7"
val springRetryVersion = "2.0.0"
val jsonApiVersion = "1.10.1"
val logstashLogbackEncoderVersion = "7.3"
val okhttp3Version = "4.11.0"
val awaitilityVersion = "4.2.0"
val nativeImageConfigPath = "$projectDir/src/main/resources/META-INF/native-image"
val nativeImageAccessFilterConfigPath = "./src/test/resources/native/access-filter.json"

plugins {
    val springBootVersion = "3.1.3"
    val kotlinVersion = "1.9.0"
    val testLoggerPluginVersion = "3.2.0"
    val springBootDependencyManagementPluginVersion = "1.1.0"
    val buildToolsNativeVersion = "0.9.21"

    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version springBootDependencyManagementPluginVersion
    id("com.adarshr.test-logger").version(testLoggerPluginVersion)
    id("org.graalvm.buildtools.native") version buildToolsNativeVersion
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs(
                "-H:+ReportExceptionStackTraces",
                "-H:EnableURLProtocols=http,https",
            )
        }
    }
}

group = "com.app"
version = "1.0.0"
java.sourceCompatibility = jdkVersion

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    mavenLocal()
}

java {
    sourceCompatibility = jdkVersion
    targetCompatibility = jdkVersion

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonDatabindVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonDataFormatVersion")
    implementation("io.vavr:vavr-kotlin:$vavrVersion")
    implementation("org.flywaydb:flyway-core")
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")
    implementation("org.hibernate.orm:hibernate-community-dialects:$hibernateCommunityDialectsVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("org.springframework.retry:spring-retry:$springRetryVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttp3Version")
    implementation("com.slm-dev:jsonapi-simple:$jsonApiVersion")

    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")

    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    implementation("io.kotest:kotest-assertions-core:$kotestVersion")
    implementation("io.kotest:kotest-property:$kotestVersion")
    implementation("io.kotest:kotest-assertions-json:$kotestVersion")
    implementation("org.junit-pioneer:junit-pioneer:$junitPioneerVersion")
    implementation("com.ninja-squad:springmockk:$springmockkVersion")
    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

allOpen {
    annotations("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embeddable")
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "ERROR")
    }
}

buildscript {
    repositories {
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = jdkVersion.toString()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    jvmArgs = listOf(
        "-agentlib:native-image-agent=access-filter-file=$nativeImageAccessFilterConfigPath,config-merge-dir=$nativeImageConfigPath"
    )
    testlogger {
        setTheme("mocha")
    }
    testLogging {
        events = setOf(
            TestLogEvent.STARTED,
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_ERROR,
            TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = false
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "ERROR")
    }
}

springBoot {
    buildInfo()
}
