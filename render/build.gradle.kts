import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.11"
    id("com.github.johnrengelman.shadow") version "4.0.2"
    id("org.jmailen.kotlinter") version "1.20.1"
}

application {
    mainClassName = "io.github.spair.tauwebmap.RenderKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "io.github.spair", name = "byond-dmm-util", version = "1.0.1")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
