package dev.patrickgold.florisboard.background.view.keyboard.repository

import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.keyboard.AbstractKeyData
import dev.patrickgold.florisboard.ime.popup.PopupSet
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import dev.patrickgold.florisboard.repository.PrefsReporitory

typealias Character = Pair<Int, String>

object BottomRightCharacterRepository {

    val defaultBottomRightCharacter =
        46 to "."
    val mainPopupRightBottomCharacter =
        44 to ","
    val bottomRightCharacters = arrayOf(
        38 to "&",
        37 to "%",
        43 to "+",
        34 to "\"",
        45 to "-",
        58 to ":",
        39 to "'",
        64 to "@",
        59 to ";",
        47 to "/",
        40 to "(",
        41 to ")",
        35 to "#",
        33 to "!",
        63 to "?"
    )

    var selectedBottomRightCharacterCode: Int
        get() = PrefsReporitory.Settings.specialSymbol
        set(value) { PrefsReporitory.Settings.specialSymbol = value }

    val selectedBottomRightCharacterLabel: String
        get() = bottomRightCharacters.firstOrNull { it.first == selectedBottomRightCharacterCode }?.second ?: mainPopupRightBottomCharacter.second

    val isDefaultBottomRightCharacter: Boolean
        get() = selectedBottomRightCharacterCode == defaultBottomRightCharacter.first

    fun getCorrectPopupSet(): PopupSet<AbstractKeyData> = when (selectedBottomRightCharacterCode) {
        defaultBottomRightCharacter.first -> PopupSet(
            mainPopupRightBottomCharacter.toTextKeyData(),
            bottomRightCharacters.map { it.toTextKeyData() }
        )
        mainPopupRightBottomCharacter.first -> PopupSet(
            defaultBottomRightCharacter.toTextKeyData(),
            bottomRightCharacters.map { it.toTextKeyData() }
        )
        else -> PopupSet(
            mainPopupRightBottomCharacter.toTextKeyData(),
            bottomRightCharacters.map { character ->
                (character.takeIf { it.first != selectedBottomRightCharacterCode } ?: defaultBottomRightCharacter)
                    .toTextKeyData()
            }
        )
    }

    private fun Character.toTextKeyData() = TextKeyData(code = first, label = second)

    enum class SelectableCharacter(val code: Int, val label: String, val displayName: Int) {
        DOT(46 , ".", R.string.special_symbols_editor_display_name_dot),
        QUESTION(63 , "?", R.string.special_symbols_editor_display_name_question),
        EXCLAMATION(33 , "!", R.string.special_symbols_editor_display_name_exclamation),
        HASH(35 , "#", R.string.special_symbols_editor_display_name_hash);

        companion object {
            fun from(code: Int) = values().first { it.code == code }
        }

    }

}
