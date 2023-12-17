import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.21"
}

group = "github.rainbowmori"
version = "1.0.0"

repositories {
    mavenCentral()

    maven ( url = "https://repo.papermc.io/repository/maven-public/" )
    maven ( url = "https://oss.sonatype.org/content/groups/public/" )
    maven ( url = "https://jitpack.io" )
    maven ( url = "https://repo.onarandombox.com/content/groups/public/" )
    maven ( url = "https://repo.minebench.de/" )
    maven ( url = "https://repo.codemc.org/repository/maven-public/" )
    maven ( url = "https://libraries.minecraft.net" )
}

val anvilGUI = "net.wesjd:anvilgui:1.9.0-SNAPSHOT"
val commandAPI = "dev.jorel:commandapi-bukkit-shade:9.2.0"
val paperLib = "io.papermc:paperlib:1.0.8"
val nbtAPI = "de.tr7zw:item-nbt-api:2.12.0"

val relocates = mapOf(
        "dev.jorel.commandapi" to "github.rainbowmori.ofro.dependencies.commandapi",
        "io.papermc.lib"      to "github.rainbowmori.ofro.dependencies.paperlib",
        "net.wesjd.anvilgui"  to "github.rainbowmori.ofro.dependencies.anvilgui",
        "de.tr7zw.changeme.nbtapi"  to "github.rainbowmori.ofro.dependencies.nbtapi"
)

val buildFileName = "${project.name}.jar"

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.onarandombox.multiversecore:multiverse-core:4.3.9")
    compileOnly("com.onarandombox.multiverseinventories:multiverse-inventories:4.2.5")
    compileOnly("com.onarandombox.multiverseportals:multiverse-portals:4.2.2")
    compileOnly("com.onarandombox.multiversenetherportals:Multiverse-NetherPortals:4.2.1")

    compileOnly("com.mojang:brigadier:1.0.18")
    compileOnly(anvilGUI)
    compileOnly(commandAPI)
    compileOnly(paperLib)
    compileOnly(nbtAPI)

    shadow(anvilGUI)
    shadow(commandAPI)
    shadow(paperLib)
    shadow(nbtAPI)

    implementation("com.github.TeamOfro:OfroLib:main-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")


}


val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    shadowJar {
        archiveFileName.set(buildFileName)
        archiveClassifier.set("")

        //configurations = listOf(project.configurations.shadow as Configuration)

        relocates.forEach { originalPackage, relocatedPackage -> relocate(originalPackage, relocatedPackage )}
    }
   withType<JavaCompile> {
       options.encoding = "UTF-8"
   }
    javadoc {
        options.encoding = "UTF-8"
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}