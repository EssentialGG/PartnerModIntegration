plugins {
    id("gg.essential.loom")
    id("gg.essential.defaults")
    id("com.gradleup.shadow") version "8.3.5"
}

version = "1.0.0"
java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

tasks.jar {
    manifest.attributes(
        // The main entry point of the Essential Ad mod are its mixins.
        // Note that you may have to re-declare your own mixin configs here too depending on your build system.
        "MixinConfigs" to "com/example/mod/essentialad/mixins.json,mixins.examplemod.json",
    )
}


val essentialAdDep = project(":1.16.5-forge") // "gg.essential:ad-1.16.5-forge:1.0.0"
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
