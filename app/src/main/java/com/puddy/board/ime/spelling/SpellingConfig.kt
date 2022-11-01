package com.puddy.board.ime.spelling

import com.puddy.board.common.RegexSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpellingConfig(
    @SerialName("basePath")
    val basePath: String,
    @SerialName("importSources")
    val importSources: List<ImportSource>
) {
    companion object {
        fun default() = SpellingConfig("ime/spelling", listOf())
    }

    @Serializable
    data class ImportSource(
        @SerialName("id")
        val id: String,
        @SerialName("label")
        val label: String,
        @SerialName("url")
        val url: String? = null,
        @SerialName("format")
        val format: ImportFormat
    )

    sealed interface ImportFormat {
        @Serializable
        @SerialName("archive")
        data class Archive(
            @SerialName("file")
            val file: FileInput,
        ) : ImportFormat

        @Serializable
        @SerialName("raw")
        data class Raw(
            @SerialName("affFile")
            val affFile: FileInput,
            @SerialName("dicFile")
            val dicFile: FileInput
        ) : ImportFormat
    }

    @Serializable
    data class FileInput(
        @SerialName("name")
        @Serializable(with = RegexSerializer::class)
        val fileNameRegex: Regex,
        @SerialName("isRequired")
        val isRequired: Boolean
    )
}
