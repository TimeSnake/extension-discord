plugins {
    id("java")
    id("java-base")
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.timesnake"
version = "2.0.0"
var projectId = 31

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }
    maven {
        url = uri("https://git.timesnake.de/api/v4/groups/7/-/packages/maven")
        name = "timesnake"
        credentials(PasswordCredentials::class)
    }
}

dependencies {
    implementation("club.minnced:opus-java:1.1.1")
    implementation("net.dv8tion:JDA:4.4.0_352")

    compileOnly("de.timesnake:basic-proxy:2.+")

    compileOnly("de.timesnake:database-proxy:3.+")
    compileOnly("de.timesnake:database-api:3.+")

    compileOnly("de.timesnake:channel-proxy:5.+")
    compileOnly("de.timesnake:channel-api:5.+")

    compileOnly("de.timesnake:library-commands:2.+")
    compileOnly("de.timesnake:library-permissions:2.+")
    compileOnly("de.timesnake:library-basic:2.+")
    compileOnly("de.timesnake:library-chat:2.+")

    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")

    compileOnly("org.apache.logging.log4j:log4j-api:2.22.1")
    compileOnly("org.apache.logging.log4j:log4j-core:2.22.1")
}

configurations.configureEach {
    resolutionStrategy.dependencySubstitution {
        if (project.parent != null) {
            substitute(module("de.timesnake:basic-proxy")).using(project(":cores:basic-proxy"))

            substitute(module("de.timesnake:database-proxy")).using(project(":database:database-proxy"))
            substitute(module("de.timesnake:database-api")).using(project(":database:database-api"))

            substitute(module("de.timesnake:channel-proxy")).using(project(":channel:channel-proxy"))
            substitute(module("de.timesnake:channel-api")).using(project(":channel:channel-api"))

            substitute(module("de.timesnake:library-commands")).using(project(":libraries:library-commands"))
            substitute(module("de.timesnake:library-permissions")).using(project(":libraries:library-permissions"))
            substitute(module("de.timesnake:library-basic")).using(project(":libraries:library-basic"))
            substitute(module("de.timesnake:library-chat")).using(project(":libraries:library-chat"))
        }
    }
}

tasks.register<Copy>("exportAsPlugin") {
    from(layout.buildDirectory.file("libs/${project.name}-${project.version}-all.jar"))
    into(findProperty("timesnakePluginDir") ?: "")

    dependsOn("shadowJar")
}

tasks.withType<PublishToMavenRepository> {
    finalizedBy("shadowJar")
}

publishing {
    repositories {
        maven {
            url = uri("https://git.timesnake.de/api/v4/projects/$projectId/packages/maven")
            name = "timesnake"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}