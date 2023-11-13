import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.1.5"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("org.openapi.generator") version "7.1.0"
    id("com.github.ben-manes.versions") version "0.49.0"
    id("name.remal.sonarlint") version "3.3.12"
}

group = "tech.skagedal"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

private val testcontainersVersion = "1.19.1"

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.flywaydb:flyway-core")
    // Because of https://github.com/spring-projects/spring-framework/issues/25095
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation(platform("org.testcontainers:testcontainers-bom:${testcontainersVersion}"))
    testImplementation("net.sourceforge.htmlunit:htmlunit:2.70.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Requirements of the openapi-generator generated code that aren't already given to us by Spring
    testImplementation("org.openapitools:jackson-databind-nullable:0.2.6")
}

java {
    toolchain {
        // When upgrading Java version, first make sure that it is available on the server.
        // See: https://blog.skagedal.tech/2023/01/01/writing-a-habit-tracker.html

        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.compileJava {
    options.compilerArgs.add("-Xlint:unchecked")
    options.compilerArgs.add("-Werror")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        csv.required.set(true)
    }
}

jacoco {
    toolVersion = "0.8.9"
}

tasks.openApiValidate {
    inputSpec.set("openapi.yaml")
}

tasks.check {
    dependsOn(tasks.openApiValidate)
}

openApiGenerate {
    inputSpec.set("openapi.yaml")
    generatorName.set("java")
    outputDir.set(layout.buildDirectory.dir("generated/sources/openapi").get().toString())
    configOptions.set(mapOf(
        "library" to "native",
        "useJakartaEe" to "true"
    ))
}

tasks.compileTestJava {
    dependsOn(tasks.openApiGenerate)
}

sourceSets {
    test {
        java {
            srcDir(layout.buildDirectory.dir("generated/sources/openapi/src/main/java"))
        }
    }
}

