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
val vertxVersion: String by project
val jacksonVersion: String by project
val kotlinCoroutineVersion: String by project
dependencies {

        api(project(":easy-vertx-bom"))
//    //vertx
//    api(group = "io.vertx", name = "vertx-core", version = vertxVersion)
//    api(group = "io.vertx", name = "vertx-lang-kotlin", version = vertxVersion)
//    api(group = "io.vertx", name = "vertx-lang-kotlin-coroutines", version = vertxVersion)
//    api(group = "io.vertx", name = "vertx-config", version = vertxVersion)
//    api(group = "io.vertx", name = "vertx-config-yaml", version = vertxVersion){
//        exclude("com.fasterxml.jackson.core:jackson-databind")
//    }

//    //jackson
//    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
//    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion"){
//        exclude("org.jetbrains.kotlin" , "kotlin-reflect")
//    }
//    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
//    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
//    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    //log
//    api("org.slf4j:slf4j-api:1.7.32")
//
//
//    //kotlin
//    api(kotlin("stdlib"))
//    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutineVersion)
//    api(kotlin("reflect"))

    testApi("ch.qos.logback", "logback-classic", "1.2.5")
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

