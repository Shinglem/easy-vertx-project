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
//    api(project(":easy-vertx-core"))
    api(kotlin("reflect"))
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
