plugins {
    java
    kotlin("jvm")
}

group = "io.github.shinglem.esay-vertx-project"
val projectVersion :String by project
version = projectVersion

repositories {
    mavenCentral()
}

dependencies {

    api( platform(project(":easy-vertx-bom")))

    //jackson
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")


    //vertx
    api(group = "io.vertx", name = "vertx-core")
    api(group = "io.vertx", name = "vertx-lang-kotlin")
    api(group = "io.vertx", name = "vertx-lang-kotlin-coroutines")
    api(group = "io.vertx", name = "vertx-config")
    api(group = "io.vertx", name = "vertx-config-yaml")


    //log
    api("io.github.microutils:kotlin-logging-jvm")

    //util
    api("com.jayway.jsonpath:json-path:2.7.0")

    //kotlin
    api(kotlin("stdlib"))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core")
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8")
    api(kotlin("reflect"))



    testApi("ch.qos.logback", "logback-classic", "1.2.11")
    testApi("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testApi("org.junit.jupiter:junit-jupiter-engine")
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

