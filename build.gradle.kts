import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Aru!DB
plugins {
    kotlin("jvm") version "1.3.72"
    maven
    `maven-publish`
    id("com.github.ben-manes.versions") version "0.21.0"
    id("com.jfrog.bintray") version "1.8.4"
}

group = "pw.aru.psi"
version = "2.0"

//Repositories and Dependencies
repositories {
    jcenter()
    maven { url = uri("https://dl.bintray.com/arudiscord/maven") }
    maven { url = uri("https://dl.bintray.com/arudiscord/kotlin") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile("com.mewna:catnip:2.0.0")
    compile("io.reactivex.rxjava3:rxkotlin:3.0.0")
    compile("io.github.classgraph:classgraph:4.8.47")
    compile("org.kodein.di:kodein-di-generic-jvm:6.5.5")
    compile("pw.aru.libs:kodein-jit-bindings:2.2")

    // Open-Source Libraries
    compile("pw.aru.libs:catnip-entityfinder:1.0")
    compile("pw.aru.libs:resources:1.0")

    // Logging
    compile("ch.qos.logback:logback-classic:1.2.3")
    compile("io.github.microutils:kotlin-logging:1.7.9")

    testCompile("org.junit.jupiter:junit-jupiter-api:5.6.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val sourceJar = task("sourceJar", Jar::class) {
    dependsOn(tasks["classes"])
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications.create("mavenJava", MavenPublication::class.java) {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()

        from(components["java"])
        artifact(sourceJar)
    }
}

fun findProperty(s: String) = project.findProperty(s) as String?
bintray {
    user = findProperty("bintrayUsername")
    key = findProperty("bintrayApiKey")
    publish = true
    setPublications("mavenJava")
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "aru"
        name = project.name
        userOrg = "arudiscord"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/arudiscord/psi.git"
    })
}

tasks.withType<BintrayUploadTask> {
    dependsOn("build", "publishToMavenLocal")
}

project.run {
    file("src/main/kotlin/pw/aru/psi/exported/exported.kt").run {
        parentFile.mkdirs()
        createNewFile()
        writeText(
            """
@file:JvmName("PsiExported")
@file:Suppress("unused")

/*
 * file "exported.kt". DO NOT EDIT MANUALLY. THIS FILE IS GENERATED BY GRADLE.
 */

package pw.aru.psi.exported

/**
 * Psi Version
 */
const val psi_version = "$version"
""".trim()
        )
    }
}