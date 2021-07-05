plugins {
    application
}

application {
    mainClass.set("org.spectralpowered.launcher.Launcher")
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
    mainClass.set("org.spectralpowered.launcher.Launcher")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":logger"))
    implementation("com.formdev:flatlaf:_")
    implementation("com.formdev:flatlaf-intellij-themes:_")
}