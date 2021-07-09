import com.google.protobuf.gradle.*

plugins {
    id("com.google.protobuf") version "0.8.16"
    `java-library`
}

dependencies {
    api("io.grpc:grpc-protobuf:1.37.0")
    api("com.google.protobuf:protobuf-java-util:3.15.8")
    api("io.grpc:grpc-kotlin-stub:1.1.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.15.8"
    }

    plugins {
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.1.0:jdk7@jar"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpckt")
            }
        }
    }
}

java {
    sourceSets.getByName("main").resources.srcDir("src/main/proto")
}