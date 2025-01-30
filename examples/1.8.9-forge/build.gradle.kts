plugins {
    id("gg.essential.loom")
    id("gg.essential.defaults")
    id("com.gradleup.shadow") version "8.3.5"
}

version = "1.0.0"
java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

tasks.jar {
    manifest.attributes(
        // The main entry point of the Essential Ad mod is its core mod:
        "FMLCorePlugin" to "com.example.mod.essentialad.asm.EssentialAdCoreMod",
        // If your mod already has its own core mod, you can have the Essential Ad core mod chain-load it:
        "EssentialAdCoreModDelegate" to "com.example.mod.asm.ExampleModCoreMod",
        // In any case case, you'll likely also want to instruct Forge to load your regular mod, otherwise it'll only
        // load the core mod:
        "FMLCorePluginContainsFMLMod" to "Yes",
    )
}

val essentialAdDep = project(":1.8.9-forge") // "gg.essential:ad-1.8.9-forge:1.0.0"
val essentialAdPackage = "com.example.mod.essentialad"

val relocatedEssentialAdJar by tasks.registering(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    destinationDirectory.set(layout.buildDirectory.dir("devlibs"))
    archiveFileName.set("essentialad.jar")
    inputs.property("essentialAdPackage", essentialAdPackage)
    val configuration = project.configurations.detachedConfiguration(project.dependencies.create(essentialAdDep))
    dependsOn(configuration)
    from({ configuration.map { zipTree(it) } })
    exclude("mcmod.info", "META-INF/mods.toml", "gg/essential/ad/EssentialAdMod.class")
    relocate("gg.essential.ad", essentialAdPackage)
    filesMatching("gg/essential/ad/mixins.json") {
        filter { it.replace("gg.essential.ad", essentialAdPackage) }
    }
}

tasks.jar {
    from(relocatedEssentialAdJar.map { it.archiveFile }.map { zipTree(it) })
}
