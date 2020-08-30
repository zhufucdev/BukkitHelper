package com.zhufucdev.bukkithelper.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkithelper.communicate.PreferenceKey

object KeyManager {
    private val list = arrayListOf<PreferenceKey>()
    val keys get() = list.toList()

    private lateinit var preference: SharedPreferences
    fun init(context: Context) {
        if (::preference.isInitialized) return

        preference = context.getSharedPreferences("key_store", Context.MODE_PRIVATE)
        val strings = preference.all

        strings.forEach { (name, value) ->
            if (Key.isKey(value as String)) {
                list.add(PreferenceKey(name, value))
            }
        }
    }

    /**
     * Construct a new random key, adding it into shared preference.
     */
    fun newRandom(name: String): Key {
        val key = PreferenceKey(name)
        add(key)
        return key
    }

    /**
     * Add a existing key to shared preference.
     */
    fun add(key: PreferenceKey) {
        if (list.contains(key)) return
        list.add(key)
        preference.edit().apply {
            putString(key.name, key.toString())
            apply()
        }
    }

    /**
     * Remove a existing [Key].
     */
    fun remove(key: PreferenceKey) {
        list.remove(key).let { if (!it) return }
        preference.edit().apply {
            remove(key.name)
            apply()
        }
    }

    /**
     * Get a existing key with name.
     * @return The key, or null if it doesn't exist.
     */
    operator fun get(name: String) = keys.firstOrNull { it.name == name }
}