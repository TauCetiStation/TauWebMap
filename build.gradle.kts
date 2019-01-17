group = "io.github.spair"
version = "1.0-SNAPSHOT"

subprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

tasks {
    withType<Wrapper> {
        gradleVersion = "5.1"
    }
}

tasks.register("cleanProject", Delete::class.java) {
    group = "build"
    description = "Deletes the build directory and project generated files."
    dependsOn(":server:clean", ":server:cleanUi", ":cli:clean")

    delete("$projectDir/build", "$projectDir/out", "$projectDir/data", "$projectDir/tmp", "$projectDir/.vertx")
}
