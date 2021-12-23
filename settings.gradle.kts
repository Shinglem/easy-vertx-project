pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
//        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://dl.bintray.com/kotlin/exposed")
        maven("https://jitpack.io")
//        jcenter()
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven("https://clojars.org/repo/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2/") }
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
//include("easy-vertx-core-default")
include("easy-vertx-web-core")
include("easy-vertx-web-spring")
