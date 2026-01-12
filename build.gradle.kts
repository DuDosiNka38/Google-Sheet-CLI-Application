plugins {
    java
    id("org.springframework.boot") version "4.0.1"
}

group = "sk.tuke"
version = "0.0.1-SNAPSHOT"
description = "Issue Track System"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.1"))
    implementation("org.springframework.boot:spring-boot-starter")

    implementation("com.google.api-client:google-api-client:2.6.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.36.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20240514-2.0.0")
    implementation("com.google.http-client:google-http-client-jackson2:1.44.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}