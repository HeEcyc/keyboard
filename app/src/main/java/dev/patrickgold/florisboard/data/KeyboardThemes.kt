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
    val id: Long = -1,
    val backgoundType: String,
    val backgroundImagePath: String? = null,
    val backgroundColor: String? = "#292E32",
    val keyFont: Int = R.font.roboto_400,
    val keyTextColor: String = "#000000",
    val radius: Int? = null,
    val strokeColor: String? = null,
    val buttonColor: String = "#484C4F",
    val imeButtonColor: String = "#373C40",
    val buttonSecondaryColor: String = "#373C40",
    val opacity: Int = 100
) : Theme(), Serializable
