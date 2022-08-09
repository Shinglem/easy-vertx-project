plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

group = "io.github.shinglem.esay-vertx-project"
val projectVersion :String by project
version = projectVersion

repositories {
    mavenCentral()
}
val springBootVersion : String by project
val jacksonVersion: String by project
dependencies {

    api(project(":easy-vertx-core"))
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-properties")
    api("org.springframework.boot:spring-boot-starter")
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
            artifactId = "easy-vertx-core-spring"


            from(components["java"])
        }
    }
}
