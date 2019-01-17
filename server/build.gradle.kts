import com.eriwen.gradle.css.tasks.MinifyCssTask
import com.eriwen.gradle.js.tasks.MinifyJsTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.11"
    id("com.github.johnrengelman.shadow") version "4.0.2"
    id("com.eriwen.gradle.js") version "2.14.1"
    id("com.eriwen.gradle.css") version "2.14.0"
    id("org.jmailen.kotlinter") version "1.20.1"
}

application {
    mainClassName = "io.vertx.core.Launcher"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    val vertxVersion = "3.6.2"
    implementation(group = "io.vertx", name = "vertx-core", version = vertxVersion)
    implementation(group = "io.vertx", name = "vertx-web", version = vertxVersion)
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
            attributes["Version"] = rootProject.version
            attributes["Build-Jdk"] =
                    "${System.getProperties()["java.version"]} (${System.getProperties()["java.vendor"]} ${System.getProperties()["java.vm.version"]})"
        }
    }

    withType<MinifyJsTask> {
        source = fileTree("$projectDir/src/main/ui/script.js")
        setDest("$projectDir/src/main/ui/dist/script.min.js")
    }

    withType<MinifyCssTask> {
        source = fileTree("$projectDir/src/main/ui/style.css")
        setDest("$projectDir/src/main/ui/dist/style.min.css")
    }
}

tasks["jar"].dependsOn("buildUi")

tasks.register("cleanUi", Delete::class.java) {
    group = "build"
    description = "Deletes weberoot folder with generated content for UI."

    delete("$projectDir/src/main/resources/webroot", "$projectDir/src/main/ui/dist")
}

tasks.register("buildUi", Copy::class.java) {
    group = "build"
    description = "Copy generated content for UI to webroot folder."
    destinationDir = File("$projectDir/src/main/resources")
    dependsOn("cleanUi", "minifyJs", "minifyCss")

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

    from("$projectDir/src/main/ui/space.png") {
        into("webroot/static")
    }
}