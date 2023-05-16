plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {

    api(platform(project(":easy-vertx-bom")))

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
    api("io.github.microutils","kotlin-logging-jvm")

    //util
    api("com.jayway.jsonpath" , "json-path").exclude("org.slf4j" , "slf4j-api")
    api("com.cronutils:cron-utils:9.2.0")

    //kotlin
    api(kotlin("stdlib"))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core")
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8")
    api(kotlin("reflect"))



    testApi("ch.qos.logback", "logback-classic", "1.4.6").exclude("org.slf4j" , "slf4j-api")
    testApi("org.junit.jupiter:junit-jupiter-api:5.9.1")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = "easy-vertx-core"


            from(components["java"])
        }
    }
}
