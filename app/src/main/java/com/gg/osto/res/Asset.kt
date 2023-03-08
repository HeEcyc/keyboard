package com.gg.osto.res

/**
 * Interface for an Asset to use within FlorisBoard. An asset is everything from a dictionary to a
 * keyboard layout to a extended popup mapping, etc. Assets are very important for the splitting
 * FlorisBoard's resources into assets.
 */
interface Asset {
    /**
     * The name of the Asset, must be unique throughout all Assets. Is used to internally identify
     * and sort the Asset. This name is non-translatable and thus is a static string.
     */
    val name: String

    /**
     * The display name of the Asset. This is the label which will be shown to the user in the
     * Settings UI. Currently also a static string.
     * TODO: make this string localize-able
     */
    val label: String

    /**
     * A list of authors who actively worked on the content of this Asset. Any content of string is
     * valid, but the best practice is to use the GitHub username.
     */
    val authors: List<String>
}
