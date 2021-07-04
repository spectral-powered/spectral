import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    kotlin("jvm") version "1.5.20"
    id("org.cadixdev.licenser") version "0.5.0"
}

tasks.withType<Wrapper> {
    gradleVersion = "7.1"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "org.spectralpowered"
    version = "1.0.0"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }
}
