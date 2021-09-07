package dev.patrickgold.florisboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

sealed class Theme

object NewTheme : Theme()

@Entity
data class KeyboardTheme(
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,
    val backgoundType: String,
    val backgroundImagePath: String?,
    val backgroundColor: String?,
    val keyFont: Int,
    val keyColor: String,
    val radius: Int?,
    val strokeColor: String?,
    val buttonColor: String
) : Theme(), Serializable
