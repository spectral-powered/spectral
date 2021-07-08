dependencies {
    implementation(project(":common"))
    implementation(project(":logger"))
    implementation("com.formdev:flatlaf:_")
    implementation("com.formdev:flatlaf-intellij-themes:_")
    implementation("net.java.dev.jna:jna:_")
    implementation("net.java.dev.jna:jna-platform:_")
}

tasks.register("generateJniHeaders", JavaCompile::class) {
    classpath = sourceSets["main"].compileClasspath
    destinationDir = file("${buildDir}/generated/jni/")
    source = sourceSets["main"].java.apply {
        options.compilerArgs.addAll(listOf(
            "-h", file("${buildDir}/generated/jni").path,
            "-d", file("${buildDir}/generated/jni-tmp").path
        ))
    }
    doLast {
        delete(file("${buildDir}/generated/jni-tmp"))
    }
}