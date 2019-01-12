import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("com.github.johnrengelman.shadow") version "4.0.2"
    id("org.jmailen.kotlinter") version "1.20.1"
    kotlin("jvm") version "1.3.11"
}

application {
    group = "io.github.spair"
    version = "1.0-SNAPSHOT"
    mainClassName = "io.vertx.core.Launcher"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    val vertxVersion = "3.6.2"
    implementation(group = "io.vertx", name = "vertx-core", version = vertxVersion)
    implementation(group = "io.vertx", name = "vertx-web", version = vertxVersion)
    
    implementation(group = "io.github.spair", name = "byond-dmm-util", version = "1.0.1")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<ShadowJar> {
        archiveFileName.set("${rootProject.name}.jar")
        manifest {
            attributes["Main-Verticle"] = "io.github.spair.tauwebmap.MainVerticle"
            attributes["License"] = "MIT License"
            attributes["Version"] = archiveVersion.get()
            attributes["Build-Jdk"] = "${System.getProperties()["java.version"]} (${System.getProperties()["java.vendor"]} ${System.getProperties()["java.vm.version"]})"
        }
    }

    withType<Wrapper> {
        gradleVersion = "5.1"
    }
}

tasks["build"].dependsOn("copyUI")

tasks.register("cleanUI", Delete::class.java) {
    group = "build"
    description = "Deletes weberoot folder with generated content for UI."

    delete("$projectDir/src/main/resources/webroot")
}

tasks.register("copyUI", Copy::class.java) {
    group = "build"
    description = "Copy generated content for UI to webroot folder."
    destinationDir = File("$projectDir/src/main/resources")
    dependsOn("cleanUI")

    val currentTimeStamp = System.currentTimeMillis()

    from("$projectDir/src/main/ui") {
        into("webroot")
        include("index.html")
        filter {
            it.replace("@{version}", "$version-build:$currentTimeStamp")
        }
    }

    from("$projectDir/src/main/ui/dist") {
        into("webroot/static")
    }
}

tasks.register("cleanProject", Delete::class.java) {
    group = "build"
    description = "Deletes the build directory and project generated files."
    dependsOn("clean", "cleanUI")

    delete("$projectDir/out", "$projectDir/data", "$projectDir/tmp","$projectDir/.vertx")
}
