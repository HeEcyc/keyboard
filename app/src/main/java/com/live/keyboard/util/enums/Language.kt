package com.live.keyboard.util.enums

import com.live.keyboard.repository.PrefsReporitory

enum class Language(val languageName: String) {
    DE("Deutsch"),
    EN("English"),
    ES("Español"),
    FR("Français"),
    IT("Italiano"),
    PT("Português"),
    RU("Русский");

    var isSelected: Boolean by PrefsReporitory.Settings.Language

    companion object {

        fun from(code: String) = valueOf(code.uppercase())
    }
}
