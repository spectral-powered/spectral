plugins {
    application
}

application {
    mainClass.set("org.spectralpowered.client.SpectralClient")
}

tasks.named<JavaExec>("run") {
    group = "spectral"
    workingDir = rootProject.projectDir
    mainClass.set("org.spectralpowered.client.SpectralClient")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":logger"))
}