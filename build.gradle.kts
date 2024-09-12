plugins {
    kotlin("jvm") version "2.0.10"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("mysql:mysql-connector-java:8.0.29")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}