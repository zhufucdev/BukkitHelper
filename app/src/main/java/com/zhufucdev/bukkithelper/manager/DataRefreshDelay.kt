package com.zhufucdev.bukkithelper.manager

import android.content.Context
import android.content.SharedPreferences
import com.zhufucdev.bukkit_helper.DynamicRefreshInterval
import java.util.*

object DataRefreshDelay : DynamicRefreshInterval {
    private lateinit var preference: SharedPreferences
    fun init(context: Context) {
        if (::preference.isInitialized) return

        preference = context.getSharedPreferences("refresh_delay", Context.MODE_PRIVATE)
    }

    private val listeners = hashMapOf<String, MutableList<(Int) -> Unit>>()

    override fun addDelayChangeListener(name: String, l: (Int) -> Unit) {
        val list = listeners[name]
        if (list == null) {
            listeners[name] = mutableListOf(l)
        } else if (!list.contains(l)) {
            list.add(l)
        }
    }

    override fun removeDelayChangeListener(type: String, l: (Int) -> Unit) {
        listeners[type]?.remove(l)
    }

    override fun clearDelayListener(type: String) {
        listeners.remove(type)
    }

    override operator fun get(type: String) = preference.getInt(type.toLowerCase(Locale.ENGLISH), 500)

    operator fun set(name: String, value: Int) {
        preference.edit().apply {
            putInt(name.toLowerCase(Locale.ENGLISH), value)
            apply()
        }
    }
}