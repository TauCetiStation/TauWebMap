import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.11"
    id("com.github.johnrengelman.shadow") version "4.0.2"
    id("org.jmailen.kotlinter") version "1.20.1"
}

application {
    mainClassName = "io.github.spair.tauwebmap.ServerKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    val http4kVersion = "3.107.0"
    implementation(group = "org.http4k", name = "http4k-core", version = http4kVersion)
    implementation(group = "org.http4k", name = "http4k-server-netty", version = http4kVersion)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks["processResources"].dependsOn("copyUi")

tasks.register("copyUi", Copy::class.java) {
    group = "build"
    description = "Copy UI files generated in :ui module to resources folder."
    dependsOn(":ui:buildUi")
    from("${project(":ui").projectDir}/src/main/webroot")
    into("src/main/resources/webroot")
}

tasks.register("cleanUi", Delete::class.java) {
    group = "build"
    description = "Deletes UI files from resources."
    delete("src/main/resources/webroot")
}
