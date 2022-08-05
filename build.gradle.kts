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
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://dl.bintray.com/kotlin/exposed")
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://clojars.org/repo/")
        mavenCentral()
    }
}

val vertxVersion: String by project
val jacksonVersion: String by project
val kotlinCoroutineVersion: String by project
dependencies {


    //vertx
    api(group = "io.vertx", name = "vertx-core", version = vertxVersion)
    api(group = "io.vertx", name = "vertx-lang-kotlin", version = vertxVersion)
    api(group = "io.vertx", name = "vertx-lang-kotlin-coroutines", version = vertxVersion)

    //jackson
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion"){
        exclude("org.jetbrains.kotlin" , "kotlin-reflect")
    }
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    //log
    api("org.slf4j:slf4j-api:1.7.32")


    //kotlin
    api(kotlin("stdlib"))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutineVersion)
    api(kotlin("reflect"))

    testApi("ch.qos.logback", "logback-classic", "1.2.5")
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


