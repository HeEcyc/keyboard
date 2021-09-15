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

    val dictionaryJSONAsset by lazy {
        when (this) {
            EN -> "ime/dict/data.json"
            RU -> "ime/dict/ru_wordlist.json"
            ES -> "ime/dict/de_wordlist.json"
            FR -> "ime/dict/es_wordlist.json"
            IT -> "ime/dict/fr_wordlist.json"
            PT -> "ime/dict/it_wordlist.json"
            DE -> "ime/dict/pt_PT_wordlist.json"
        }
    }

}
