package com.zhufucdev.bukkit_helper

/**
 * User preferred intervals between each two adjacent actions where a particular plugin fetches data from server.
 *
 * Use [Context.dynamicRefreshInterval] to get the instance.
 */
interface DynamicRefreshInterval {
    operator fun get(type: String): Int
    fun addDelayChangeListener(name: String, l: (Int) -> Unit)
    fun removeDelayChangeListener(type: String, l: (Int) -> Unit)
    fun clearDelayListener(type: String)

    object KnownName {
        const val TPS = "tps"
    }
}