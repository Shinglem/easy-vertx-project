pluginManagement {
    repositories {
//        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        gradlePluginPortal()
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://clojars.org/repo/")
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2/") }
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    val kotlinVersion:String by settings

    plugins {

        id( "org.jetbrains.kotlin.jvm")  version kotlinVersion
        id ("org.jetbrains.kotlin.plugin.noarg")  version kotlinVersion
        id ("org.jetbrains.kotlin.plugin.allopen")  version kotlinVersion
        kotlin("kapt") version kotlinVersion

    }

}



rootProject.name = "easy-vertx-project"
include("easy-vertx-core")
include("easy-vertx-core-spring")
include("easy-vertx-web")
include("easy-vertx-web-spring")
include("easy-vertx-bom")
include("pool-log-tracer")
include("log-tracer")
