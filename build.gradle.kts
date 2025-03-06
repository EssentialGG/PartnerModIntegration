import gg.essential.ad.build.DownloadContainerTask
import gg.essential.gradle.util.*

plugins {
    id("gg.essential.defaults")
    id("gg.essential.defaults.maven-publish")
    id("gg.essential.multi-version")
}

group = "gg.essential"
version = "1.0.0"

repositories {
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

loom.noServerRunConfigs()

loom.mixin.useLegacyMixinAp.set(false)

loom.runs {
    named("client") {
        if (platform.mcVersion <= 11202) {
            property("fml.coreMods.load", "gg.essential.ad.asm.EssentialPartnerCoreMod")
        }
        if (platform.mcVersion >= 11600 && platform.isForge) {
            programArgs("--mixin", "gg/essential/ad/mixins.json")
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
            12103 -> "0.106.1+1.21.3"
            12104 -> "0.115.1+1.21.4"
            else -> error("Unable to determine fabric api version")
        }

        modImplementation(include(fabricApi.module("fabric-api-base", fabricApiVersion))!!)
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
        12104 -> "1.4.2+fabric-1.21.4"
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
        // Note: Using jarx extension to workaround https://github.com/GradleUp/shadow/issues/111
        rename { "gg/essential/ad/container.jarx" }
    }
    inputs.property("version", { project.version })
    filesMatching("gg/essential/ad/loader/version.txt") {
        filter { _ -> project.version.toString() }
    }
}

tasks.jar {
    manifest.attributes(
        "Implementation-Vendor" to "ModCore Inc.",
        "Implementation-Title" to "EssentialPartnerModIntegration",
        "Implementation-Version" to version,
    )
    if (platform.isModLauncher) {
        manifest.attributes(
            "MixinConfigs" to "gg/essential/ad/mixins.json"
        )
    }
    if (platform.mcVersion <= 11202) {
        manifest.attributes(
            "FMLCorePlugin" to "gg.essential.ad.asm.EssentialPartnerModCoreMod",
            "FMLCorePluginContainsFMLMod" to "Yes",
        )
    }
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "partner-mod-integration-$platform"
        }
    }
}
