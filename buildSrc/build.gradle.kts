plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven(url = "https://maven.fabricmc.net/")
    maven(url = "https://maven.minecraftforge.net")
    maven(url = "https://maven.architectury.dev/")
    maven(url = "https://repo.essential.gg/repository/maven-public")
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("gg.essential:essential-gradle-toolkit:0.6.4")
}

kotlin {
    jvmToolchain(21)
}
