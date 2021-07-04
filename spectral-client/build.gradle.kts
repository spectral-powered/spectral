plugins {
    application
}

application {
    mainClass.set("org.spectralpowered.client.Spectral")
}

tasks.named<JavaExec>("run") {
    group = "spectral"
    workingDir = rootProject.projectDir
    mainClass.set("org.spectralpowered.client.Spectral")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":logger"))
    implementation("com.formdev:flatlaf:_")
    implementation("com.formdev:flatlaf-intellij-themes:_")
}