package com.ioskey.iosboard.ime.media.emoticon

import com.ioskey.iosboard.res.AssetManager
import com.ioskey.iosboard.res.FlorisRef
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