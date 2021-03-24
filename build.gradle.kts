import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("kapt") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "ru.dude.pcscale"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.6.1")
    annotationProcessor("info.picocli:picocli:4.6.1")
}



kapt {
    arguments {
        arg("project", "${project.group}/${project.name}")
    }
}

application {
    mainClassName = "ru.dude.pcscale.MainKt"
}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.withType<Jar>(){
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        minimize()
        archiveBaseName.set("pcscale")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "ru.dude.pcscale.MainKt"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
