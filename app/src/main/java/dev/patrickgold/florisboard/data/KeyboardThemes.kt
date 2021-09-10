package dev.patrickgold.florisboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

sealed class Theme : Serializable

object NewTheme : Theme()

@Entity
data class KeyboardTheme(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var backgoundType: String? = null,
    var backgroundImagePath: String? = null,
    var backgroundColor: String? = "#292E32",
    var keyFont: Int? = null,
    var keyTextColor: String = "#FFFFFF",
    var strokeRadius: Int = 16,
    var strokeColor: String? = null,
    var buttonColor: String = "#484C4F",
    var imeButtonColor: String = "#5F97F6",
    var buttonSecondaryColor: String = "#373C40",
    var opacity: Int = 100
) : Theme(), Serializable {

    fun copyTheme(keyboardTheme: KeyboardTheme) {
        this.backgoundType = keyboardTheme.backgoundType
        this.backgroundImagePath = keyboardTheme.backgroundImagePath
        this.backgroundColor = keyboardTheme.backgroundColor
        this.keyFont = keyboardTheme.keyFont
        this.keyTextColor = keyboardTheme.keyTextColor
        this.strokeRadius = keyboardTheme.strokeRadius
        this.strokeColor = keyboardTheme.strokeColor
        this.buttonColor = keyboardTheme.buttonColor
        this.imeButtonColor = keyboardTheme.imeButtonColor
        this.buttonSecondaryColor = keyboardTheme.buttonSecondaryColor
        this.opacity = keyboardTheme.opacity
    }
}
