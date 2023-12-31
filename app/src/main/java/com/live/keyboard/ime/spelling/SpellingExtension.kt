package com.live.keyboard.ime.spelling

import com.live.keyboard.res.ext.Extension
import java.io.File

class SpellingExtension(
    config: SpellingDict.Meta,
    workingDir: File,
    flexFile: File
) : Extension<SpellingDict.Meta>(config, workingDir, flexFile) {

    val dict: SpellingDict? = SpellingDict.new(workingDir.absolutePath, config)

    override fun close() {
        dict?.dispose()
        super.close()
    }
}
