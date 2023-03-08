package com.gg.osto

import com.gg.osto.ime.core.FlorisBoard

/**
 * This class only exists to prevent accidental IME deactivation after an update
 * of FlorisBoard to a new version when the location of the FlorisBoard class has
 * changed. The Android Framework uses the service class path as the IME id,
 * using this extension here makes sure it won't change ever again for the system.
 *
 * Important: DO NOT PUT ANY LOGIC INTO THIS CLASS. Make the necessary changes
 *  within the FlorisBoard class instead.
 */
class MyImeService : FlorisBoard()
