plugins {
    java
    kotlin("jvm")
    `maven-publish`
}
val projectVersion :String by project
version = projectVersion

allprojects {
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://dl.bintray.com/kotlin/exposed")
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://clojars.org/repo/")
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


tasks {

    compileJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
    }
    compileTestJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
    }
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

}



publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.shinglem"
            artifactId = "easy-vertx-project"


            from(components["java"])
        }
    }
}


