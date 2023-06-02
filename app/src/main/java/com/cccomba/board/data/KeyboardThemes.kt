package com.cccomba.board.data

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.cccomba.board.R
import java.io.Serializable

sealed class Theme : Serializable

object NewTheme : Theme()

@Entity
data class KeyboardTheme(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @Ignore var index: Int? = null,
    var backgoundType: String? = null,
    var backgroundImagePath: String? = null,
    var backgroundColor: String? = "#292E32",
    var keyFont: Int = R.font.roboto_400,
    var keyTextColor: String = "#FFFFFF",
    var strokeRadius: Int = 6,
    var strokeColor: String? = null,
    var buttonColor: String = "#484C4F",
    var imeButtonColor: String = "#5F97F6",
    var buttonSecondaryColor: String = "#373C40",
    var opacity: Int = 100,
) : Theme(), Serializable {

    @Ignore
    var previewImage: String? = null

    @Ignore
    var isSelected: Boolean = false

    @Ignore
    val isSelectedObservable = ObservableBoolean(false)

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
        this.isSelected = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyboardTheme

        if (backgoundType != other.backgoundType) return false
        if (backgroundImagePath != other.backgroundImagePath) return false
        if (backgroundColor != other.backgroundColor) return false
        if (keyFont != other.keyFont) return false
        if (keyTextColor != other.keyTextColor) return false
        if (strokeRadius != other.strokeRadius) return false
        if (strokeColor != other.strokeColor) return false
        if (buttonColor != other.buttonColor) return false
        if (imeButtonColor != other.imeButtonColor) return false
        if (buttonSecondaryColor != other.buttonSecondaryColor) return false
        if (opacity != other.opacity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = (backgoundType?.hashCode() ?: 0)
        result = 31 * result + (backgroundImagePath?.hashCode() ?: 0)
        result = 31 * result + (backgroundColor?.hashCode() ?: 0)
        result = 31 * result + (keyFont ?: 0)
        result = 31 * result + keyTextColor.hashCode()
        result = 31 * result + strokeRadius
        result = 31 * result + (strokeColor?.hashCode() ?: 0)
        result = 31 * result + buttonColor.hashCode()
        result = 31 * result + imeButtonColor.hashCode()
        result = 31 * result + buttonSecondaryColor.hashCode()
        result = 31 * result + opacity
        return result
    }

}
