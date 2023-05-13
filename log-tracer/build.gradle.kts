plugins {
    id("java")
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

    //vertx
    api(group = "io.vertx", name = "vertx-core")
    //log
    api("io.github.microutils","kotlin-logging-jvm")
    api(kotlin("reflect"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
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
            artifactId = "log-tracer"


            from(components["java"])
        }
    }
}
