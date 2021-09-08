package dev.patrickgold.florisboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.patrickgold.florisboard.R
import java.io.Serializable

sealed class Theme

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
    var radius: Int? = null,
    var strokeColor: String? = null,
    var buttonColor: String = "#484C4F",
    var imeButtonColor: String? = "#5F97F6",
    var buttonSecondaryColor: String? = "#373C40",
    var opacity: Int = 100
) : Theme(), Serializable
