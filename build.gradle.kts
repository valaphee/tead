/*
 * Copyright (c) 2021, Valaphee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("com.palantir.git-version") version "0.12.3"
    id("edu.sc.seis.launch4j") version "2.5.0"
    kotlin("jvm") version "1.5.31"
    id("org.openjfx.javafxplugin") version "0.0.10"
    signing
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.google.inject:guice:5.0.1")
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:1.0.9")
    implementation("com.nativelibs4java:bridj:0.7.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")
    implementation("de.codecentric.centerdevice:javafxsvg:1.3.0")
    implementation("io.github.classgraph:classgraph:4.8.138")
    implementation("io.ktor:ktor-client-auth:1.6.7")
    implementation("io.ktor:ktor-client-jackson:1.6.7")
    implementation("io.ktor:ktor-client-okhttp:1.6.7")
    implementation("io.kubernetes:client-java:14.0.0")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("org.apache.sshd:sshd-netty:2.8.0")
    implementation("org.apache.sshd:sshd-sftp:2.8.0")
    implementation("org.controlsfx:controlsfx:11.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0-RC2")
    implementation("org.jfxtras:jmetro:11.6.15")
}

group = "com.valaphee"
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
val details = versionDetails()
version = "${details.lastTag}.${details.commitDistance}"

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "16"
        targetCompatibility = "16"
    }

    withType<KotlinCompile>().configureEach { kotlinOptions { jvmTarget = "16" } }

    withType<Test> { useJUnitPlatform() }

    jar {
        manifest {
            attributes(
                "Implementation-Title" to "Blit",
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Valaphee"
            )
        }
    }
}

application { mainClass.set("com.valaphee.blit.MainKt") }

javafx { modules("javafx.controls", "javafx.graphics") }

launch4j {
    mainClassName = "com.valaphee.blit.MainKt"
    icon = "${projectDir}/app.ico"
    copyright = "Copyright (c) 2021, Valaphee"
    jvmOptions = setOf("--add-opens=java.base/java.nio=ALL-UNNAMED", "--add-opens java.base/jdk.internal.misc=ALL-UNNAMED", "-Dio.netty.tryReflectionSetAccessible=true")
    companyName = "Valaphee"
    fileDescription = "Blit is a free and open-source, cross-platform WebDAV, K8s CP and SFTP client with a vast list of features."
    productName = "Blit"
}

signing { useGpgCmd() }
