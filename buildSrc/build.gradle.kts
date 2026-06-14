plugins {
    `kotlin-dsl`
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jooq:jooq-codegen:3.19.30")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.7")
    implementation("org.flywaydb:flyway-core:12.8.1")
    implementation("org.flywaydb:flyway-mysql:12.8.1")
}
