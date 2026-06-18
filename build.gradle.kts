import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    application
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("org.flywaydb.flyway") version "12.8.1"
}

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-mysql:12.8.1")
    }
}

group = "id.perumdamts"
version = "0.0.1-SNAPSHOT"
description = "kepegawaian"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass.set("id.perumdamts.kepegawaian.KepegawaianApplication")
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }

}

dependencies {
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Data
    implementation("org.springframework.data:spring-data-envers")

    // Kafka — Boot 4.0 modularized auto-config out of spring-boot-autoconfigure
    // into the dedicated spring-boot-kafka module (transitively pulls spring-kafka).
    implementation("org.springframework.boot:spring-boot-kafka")

    // Flyway
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-mysql:12.8.1")

    // Flyway Gradle plugin uses runtimeClasspath for JDBC drivers

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // Dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Database
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // Config processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // SpringDoc OpenAPI 3.x (Boot 4 compatible)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

    // WebFlux (temporary - will be removed in F6.S2)

    // Apache POI for Excel
    implementation("org.apache.poi:poi-ooxml:5.4.1")

    // Jackson
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Commons IO
    implementation("commons-io:commons-io:2.16.1")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.register<JooqCodegenTask>("jooqCodegen") {
    jdbcUrl.set("jdbc:mariadb://localhost:3307/kepegawaian")
    dbUser.set("test")
    dbPassword.set("test")
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("kepegawaian.jar")
}

tasks.named<BootRun>("bootRun") {
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

springBoot {
    buildInfo()
}

// ---- Flyway Migration Configuration ----
// Priority: .env.migrate > .env > system env > defaults
val flywayEnv: Map<String, String> = run {
    val file = listOf(".env.migrate", ".env").firstNotNullOfOrNull {
        val f = rootProject.file(it)
        if (f.exists()) f else null
    }
    if (file == null) emptyMap()
    else file.readLines()
        .filter { l -> "=" in l && !l.startsWith("#") && !l.startsWith("import") }
        .associate { l ->
            val (k, v) = l.split("=", limit = 2)
            k.trim() to v.trim()
        }
}

fun flyEnv(key: String, default: String): String =
    flywayEnv[key] ?: System.getenv(key) ?: default

flyway {
    url = "jdbc:mariadb://${flyEnv("DB_HOST", "192.168.230.84")}:" +
          "${flyEnv("DB_PORT", "3307")}/${flyEnv("DB_SCHEMA", "kepegawaian_dev_new")}" +
          "?useSSL=false&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useLegacyDatetimeCode=false"
    user = flyEnv("DB_USER", "dev")
    password = flyEnv("DB_PASSWORD", "password")
    locations = arrayOf("filesystem:src/main/resources/db/migration")
    driver = "org.mariadb.jdbc.Driver"
    cleanDisabled = false
}