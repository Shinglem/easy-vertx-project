plugins {
    id("java")
    kotlin("jvm")
}

group = "io.github.shinglem.esay-vertx-project"
val projectVersion :String by project
version = projectVersion

repositories {
    mavenCentral()
}
val vertxVersion: String by project
val jacksonVersion: String by project
val kotlinCoroutineVersion: String by project
val kotlinLoggingVersion: String by project
dependencies {

    //jackson
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")


    //vertx
    api(group = "io.vertx", name = "vertx-core", version = vertxVersion)
    api(group = "io.vertx", name = "vertx-lang-kotlin", version = vertxVersion)
    api(group = "io.vertx", name = "vertx-lang-kotlin-coroutines", version = vertxVersion)
    api(group = "io.vertx", name = "vertx-config", version = vertxVersion)
    api(group = "io.vertx", name = "vertx-config-yaml", version = vertxVersion)


    //log
    api("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

    //util
    api("com.jayway.jsonpath:json-path:2.7.0")

    //kotlin
    api(kotlin("stdlib"))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutineVersion)
    api(kotlin("reflect"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

val jdkVersion :String by project
tasks {

    compileJava {
        sourceCompatibility = jdkVersion
    }
    compileTestJava {
        sourceCompatibility = jdkVersion
    }
    compileKotlin {
        kotlinOptions.jvmTarget = jdkVersion
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = jdkVersion
    }

}

