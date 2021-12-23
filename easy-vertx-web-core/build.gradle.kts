plugins {
    java
    kotlin("jvm")
    `maven-publish`
}
group = "io.github.shinglem.easy-vertx-project"
val projectVersion :String by project
version = projectVersion
val vertxVersion: String by project
val jacksonVersion: String by project
val kotlinCoroutineVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    api(project(":easy-vertx-core"))
//    api(project(":easy-vertx-core-default"))

    //vertx
    api(group = "io.vertx", name = "vertx-web", version = vertxVersion)


    //kotlin
    api(kotlin("stdlib"))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutineVersion)
    api(kotlin("reflect"))
    testApi("ch.qos.logback", "logback-classic", "1.2.5")
//    testApi(project(":easy-vertx-core-default"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
            groupId = "io.github.shinglem.easy-vertx-project"
            artifactId = "easy-vertx-web-core"


            from(components["java"])
        }
    }
}
