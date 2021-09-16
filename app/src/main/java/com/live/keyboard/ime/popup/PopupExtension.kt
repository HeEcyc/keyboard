package com.live.keyboard.ime.popup

import com.live.keyboard.ime.keyboard.AbstractKeyData
import com.live.keyboard.res.Asset
import com.live.keyboard.ime.text.key.KeyVariation
import kotlinx.serialization.Serializable

/**
 * An object which maps each base key to its extended popups. This can be done for each
 * key variation. [KeyVariation.ALL] is always the fallback for each key.
 */
typealias PopupMapping = Map<KeyVariation, Map<String, PopupSet<AbstractKeyData>>>

/**
 * Class which contains an extended popup mapping to use for adding popups subtype based on the
 * keyboard layout.
 *
 * @property mapping The mapping of the base keys to their popups. See [PopupMapping] for more info.
 */
@Serializable
class PopupExtension(
    override val name: String,
    override val label: String = name,
    override val authors: List<String>,
    val mapping: PopupMapping
) : Asset {
    companion object {
        fun empty() = PopupExtension("", "", listOf(), mapOf())
    }
}
