plugins {
    application
    kotlin("jvm") version "2.1.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "jp.kaleidot725"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val coroutines = "1.8.0"
val mcpVersion = "0.4.0"
val slf4jVersion = "2.0.9"
val ktorVersion = "3.1.1"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines}")
    implementation("io.modelcontextprotocol:kotlin-sdk:${mcpVersion}")
    implementation("org.slf4j:slf4j-nop:${slf4jVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("jp.kaleidot725.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName.set("server")
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
}
