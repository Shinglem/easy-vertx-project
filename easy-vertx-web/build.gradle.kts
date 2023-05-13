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
    api(project(":easy-vertx-core"))
    //vertx
    api(group = "io.vertx", name = "vertx-web")
    api("com.google.guava:guava:31.1-jre")

    testApi("ch.qos.logback", "logback-classic", "1.4.5").exclude("org.slf4j" , "slf4j-api")
//    testApi(project(":easy-vertx-core-default"))
    testApi(group = "io.vertx", name = "vertx-web-client", version = vertxVersion)
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
            groupId = rootProject.group.toString()
            artifactId = "easy-vertx-web"


            from(components["java"])
        }
    }
}
