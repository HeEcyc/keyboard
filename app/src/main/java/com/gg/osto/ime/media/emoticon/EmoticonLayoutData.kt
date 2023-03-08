package com.gg.osto.ime.media.emoticon

import com.gg.osto.res.AssetManager
import com.gg.osto.res.FlorisRef
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
