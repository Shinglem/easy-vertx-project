plugins {
    java
    kotlin("jvm")
    `maven-publish`
}
val projectVersion :String by project
version = projectVersion
group = "com.github.Shinglem.easy-vertx-project"

allprojects {
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://clojars.org/repo/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

val vertxVersion: String by project
val jacksonVersion: String by project
val kotlinCoroutineVersion: String by project
dependencies {


    //kotlin
    api(kotlin("stdlib"))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutineVersion)
    api(kotlin("reflect"))

    testApi("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testApi("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
val jdkVersion : String by project

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
            artifactId = "easy-vertx-project"


            from(components["java"])
        }
    }
}


