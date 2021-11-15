package com.keyboard.neon_keyboard.ime.media.emoticon

import com.keyboard.neon_keyboard.res.AssetManager
import com.keyboard.neon_keyboard.res.FlorisRef
import kotlinx.serialization.Serializable

typealias EmoticonLayoutDataArrangement = List<List<EmoticonKeyData>>

@Serializable
data class EmoticonLayoutData(
    var type: String,
    var name: String,
    var direction: String,
    var arrangement: EmoticonLayoutDataArrangement = listOf()
) {
    companion object {
        fun fromJsonFile(path: String): EmoticonLayoutData? {
            return AssetManager.defaultOrNull()
                ?.loadJsonAsset<EmoticonLayoutData>(FlorisRef.assets(path))
                ?.getOrNull()
        }
    }
}
