package com.zhufucdev.bukkit_helper

interface DynamicRefreshInterval {
    operator fun get(type: String): Int
    fun addDelayChangeListener(name: String, l: (Int) -> Unit)
    fun removeDelayChangeListener(type: String, l: (Int) -> Unit)
    fun clearDelayListener(type: String)

    object KnownName {
        const val TPS = "tps"
    }
}