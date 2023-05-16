plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

group = rootProject.group
version = rootProject.version


val vertxVersion: String by project
val jacksonVersion: String by project
val kotlinCoroutineVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    api(project(":easy-vertx-core-spring"))
    api(project(":easy-vertx-web"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
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
            groupId = rootProject.group.toString()
            artifactId = "easy-vertx-web-spring"


            from(components["java"])
        }
    }
}
