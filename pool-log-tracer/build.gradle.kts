plugins {
    id("java")
    kotlin("jvm")
    `maven-publish`
}

group = "io.github.shinglem.esay-vertx-project"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {

    api(platform(project(":easy-vertx-bom")))

    api(project(":log-tracer"))

    api(group = "io.vertx", name = "vertx-core")
    api(group = "io.vertx", name = "vertx-sql-client")

    api("com.github.freva:ascii-table:1.8.0")

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
