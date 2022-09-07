import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.akhris"
version = "1.0-SNAPSHOT"


val exposedVersion: String by project

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                //dependency injection:
                implementation("org.kodein.di:kodein-di:7.14.0")
//                implementation("org.kodein.di:kodein-di-framework-compose:7.14.0")

                //decompose:
                implementation("com.arkivanov.decompose:decompose:0.8.0")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:0.8.0")

                //    exposed:
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

                //sqlite driver:
                implementation("org.xerial:sqlite-jdbc:3.39.2.0")

                //swaydb (for properties):
                implementation("io.swaydb:swaydb_2.13:0.16.2")

                //for qualifiers
                implementation("javax.inject:javax.inject:1")

                //export to excel
                implementation("io.github.evanrupert:excelkt:1.0.2")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "samples2"
            packageVersion = "1.0.0"
            modules("java.instrument", "java.security.jgss", "java.sql", "java.xml.crypto", "jdk.unsupported")
        }
    }
}
