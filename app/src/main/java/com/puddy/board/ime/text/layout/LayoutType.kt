package com.puddy.board.ime.text.layout

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Defines the type of the layout.
 */
@Serializable(with = LayoutTypeSerializer::class)
enum class LayoutType {
    CHARACTERS,
    CHARACTERS_MOD,
    EXTENSION,
    NUMERIC,
    NUMERIC_ADVANCED,
    NUMERIC_ROW,
    PHONE,
    PHONE2,
    SYMBOLS,
    SYMBOLS_MOD,
    SYMBOLS2,
    SYMBOLS2_MOD;

    override fun toString(): String {
        return super.toString().replace("_", "/").lowercase()
    }

    companion object {
        fun fromString(string: String): LayoutType {
            return valueOf(string.replace("/", "_").uppercase())
        }
    }
}

class LayoutTypeSerializer : KSerializer<LayoutType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LayoutType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LayoutType) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LayoutType {
        return LayoutType.fromString(decoder.decodeString())
    }
}
