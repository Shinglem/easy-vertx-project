plugins {
    `java-platform`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}
val vertxVersion: String by project
val jacksonVersion: String by project
val kotlinCoroutineVersion: String by project
val kotlinLoggingVersion: String by project
val springBootVersion : String by project
dependencies {
    constraints {
        //jackson
        api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
        api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
        api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
        api("com.fasterxml.jackson.dataformat:jackson-dataformat-properties:$jacksonVersion")

        //vertx
        api("io.vertx:vertx-core:$vertxVersion")
        api("io.vertx:vertx-lang-kotlin:$vertxVersion")
        api("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
        api("io.vertx:vertx-config:$vertxVersion")
        api("io.vertx:vertx-config-yaml:$vertxVersion")
        api("io.vertx:vertx-web:$vertxVersion")
        api("io.vertx:vertx-sql-client:$vertxVersion")
        //log
        api("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
        //util
        api("com.jayway.jsonpath:json-path:2.8.0")

//        //spring
//        api("org.springframework.boot","spring-boot-starter", springBootVersion)

        //kotlin
        api(kotlin("stdlib"))
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core$kotlinCoroutineVersion")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8$kotlinCoroutineVersion")
        api(kotlin("reflect"))


    }

}

javaPlatform {
    allowDependencies()
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = "easy-vertx-bom"


            from(components["javaPlatform"])
        }
    }
}
