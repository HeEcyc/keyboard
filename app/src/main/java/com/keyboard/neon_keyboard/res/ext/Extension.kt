package com.keyboard.neon_keyboard.res.ext

import java.io.Closeable
import java.io.File

/**
 * An extension container holding a parsed config, a working directory file
 * object as well as a reference to the original flex file.
 *
 * @property config The parsed config of this extension.
 * @property workingDir The working directory, used as a cache and as a staging
 *  area for modifications to extension files.
 * @property flexFile Optional, defines where the original flex file is stored.
 */
open class Extension<C : ExtensionConfig>(
    val config: C,
    val workingDir: File,
    val flexFile: File?
) : Closeable {

    /**
     * Closes the extension and deletes the temporary files. After invoking this
     * method, this object and its cache files must never be touched again.
     */
    override fun close() {
        workingDir.deleteRecursively()
    }
}
