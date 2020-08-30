package com.zhufucdev.bukkithelper.manager

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object DataRefreshDelay {
    private lateinit var preference: SharedPreferences
    fun init(context: Context) {
        preference = context.getSharedPreferences("refresh_delay", Context.MODE_PRIVATE)
    }

    private val listeners = hashMapOf<DataType, MutableList<(Int) -> Unit>>()

    fun addDelayChangeListener(name: DataType, l: (Int) -> Unit) {
        val list = listeners[name]
        if (list == null) {
            listeners[name] = mutableListOf(l)
        } else if (!list.contains(l)) {
            list.add(l)
        }
    }

    fun removeDelayChangeListener(name: DataType, l: (Int) -> Unit) {
        listeners[name]?.remove(l)
    }

    fun clearDelayListener(name: DataType) {
        listeners.remove(name)
    }

    operator fun get(name: DataType) = preference.getInt(name.name.toLowerCase(Locale.ENGLISH), 500)

    operator fun set(name: DataType, value: Int) {
        preference.edit().apply {
            putInt(name.name.toLowerCase(Locale.ENGLISH), value)
            apply()
        }
    }

    enum class DataType {
        TPS
    }
}