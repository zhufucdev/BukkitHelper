package com.zhufucdev.bukkit_helper.ui.data

import com.zhufucdev.bukkit_helper.ui.Component

/**
 * Represents an empty area inside which other [Component] is held.
 */
data class Space(val top: Int = 0, val bottom: Int = 0, val start: Int = 0, val end: Int = 0) {
    companion object {
        val EMPTY get() = Space()
        fun all(length: Int) = Space(length, length, length, length)
    }
}
