package gg.essential.partnermod.build

import com.google.gson.JsonParser
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.security.MessageDigest
import java.util.HexFormat

abstract class DownloadContainerTask : DefaultTask() {
    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val platform: Property<String>

    @get:OutputFile
    abstract val containerFile: RegularFileProperty

    @TaskAction
    fun download() {
        val client = HttpClient.newHttpClient()

        val metadataUri = URI.create(DOWNLOAD_URL.format(version.get(), platform.get()))
        val metadataResponse = client.send(
            HttpRequest.newBuilder(metadataUri).build(),
            BodyHandlers.ofString()
        )

        if (metadataResponse.statusCode() != 200) {
            throw RuntimeException("Failed to fetch container metadata from $metadataUri - ${metadataResponse.statusCode()} : ${metadataResponse.body()}")
        }

        val metadata = JsonParser.parseString(metadataResponse.body()).asJsonObject
        val downloadUri = URI.create(metadata["url"].asString)
        val checksum = HexFormat.of().parseHex(metadata["checksum"].asString)

        val downloadResponse = client.send(
            HttpRequest.newBuilder(downloadUri).build(),
            BodyHandlers.ofByteArray()
        )

        if (downloadResponse.statusCode() != 200) {
            throw RuntimeException("Failed to download container from $downloadUri - ${downloadResponse.statusCode()}")
        }

        val computedChecksum = md5(downloadResponse.body())
        if (!checksum.contentEquals(computedChecksum)) {
            throw RuntimeException("Container checksum mismatch! Expected $checksum, got $computedChecksum")
        }

        containerFile.get().asFile.writeBytes(downloadResponse.body())
    }

    private fun md5(bytes: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(bytes)
        return digest.digest()
    }

    companion object {
        const val DOWNLOAD_URL = "https://api.essential.gg/mods/v1/essential:container/versions/%s/platforms/%s/download"
    }
}
