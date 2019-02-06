group = "io.github.spair"
version = "1.4.1"

plugins {
    `build-scan`
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    publishAlwaysIf(System.getenv("CI") != null)
}

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
    dependsOn(":server:clean", ":server:cleanUi", ":render:clean", ":ui:cleanUi")

    delete("$projectDir/build", "$projectDir/out", "$projectDir/data", "$projectDir/tmp")
}
