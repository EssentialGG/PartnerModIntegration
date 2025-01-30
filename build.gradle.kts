import gg.essential.ad.build.DownloadContainerTask
import gg.essential.gradle.util.*

plugins {
    id("gg.essential.defaults")
    id("gg.essential.multi-version")
}

version = "1.0.0"

repositories {
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

loom.noServerRunConfigs()

loom.runs {
    named("client") {
        if (platform.mcVersion <= 11202) {
            property("fml.coreMods.load", "gg.essential.ad.asm.EssentialAdCoreMod")
        }
        if (platform.mcVersion >= 11600 && platform.isForge) {
            programArgs("--mixin", "mixins.essentialad.json")
        }
    }
}

dependencies {

    if (platform.isFabric) {
        val fabricApiVersion = when (platform.mcVersion) {
            11605 -> "0.42.0+1.16"
            11701 -> "0.46.1+1.17"
            11801 -> "0.46.6+1.18"
            11802 -> "0.77.0+1.18.2"
            11900 -> "0.58.0+1.19"
            11902 -> "0.77.0+1.19.2"
            11903 -> "0.76.1+1.19.3"
            11904 -> "0.87.2+1.19.4"
            12000 -> "0.83.0+1.20"
            12001 -> "0.92.2+1.20.1"
            12002 -> "0.91.6+1.20.2"
            12004 -> "0.97.1+1.20.4"
            12006 -> "0.100.4+1.20.6"
            12100 -> "0.100.4+1.21"
            else -> error("Unable to determine fabric api version")
        }

        modImplementation(include(fabricApi.module("fabric-api-base", fabricApiVersion))!!)
        modImplementation(include(fabricApi.module("fabric-resource-loader-v0", fabricApiVersion))!!)
        modImplementation(include(fabricApi.module("fabric-screen-api-v1", fabricApiVersion))!!)
    }

    val devAuthPlatform = when {
        platform.isFabric -> "fabric"
        platform.isLegacyForge -> "forge-legacy"
        platform.isForge -> "forge-latest"
        else -> error("Unable to determine DevAuth platform")
    }

    modLocalRuntime("me.djtheredstoner:DevAuth-${devAuthPlatform}:1.2.1")
}

val downloadContainer by tasks.registering(DownloadContainerTask::class) {
    containerFile = layout.buildDirectory.file("essential-container.jar")
    version = when (project.platform.mcVersion) {
        12103 -> "1.4.2+fabric-1.21.3"
        12102 -> "1.4.2+fabric-1.21.2"
        12101 -> "1.4.2+fabric-1.21.1"
        12100 -> "1.4.2+fabric-1.21"
        else -> "1.4.2"
    }
    platform = "${project.platform.loaderStr}_${project.platform.mcVersionStr}"
}

tasks.processResources {
    from(downloadContainer.get().containerFile) {
        rename { "gg/essential/ad/container.jar" }
    }
}

tasks.jar {
    manifest.attributes(
        "Implementation-Version" to version,
    )
    if (platform.mcVersion <= 11202) {
        manifest.attributes(
            "FMLCorePlugin" to "gg.essential.ad.asm.EssentialAdCoreMod",
            "FMLCorePluginContainsFMLMod" to "Yes",
        )
    }
}
