import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.11"
    id("com.github.johnrengelman.shadow") version "4.0.2"
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
            attributes("Main-Verticle" to "io.github.spair.tauwebmap.ViewVerticle")
        }
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
