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
        preference = context.getSharedPreferences("key_store", Context.MODE_PRIVATE)
        val strings = preference.getStringSet("keys", mutableSetOf())!!

        strings.forEach {
            if (PreferenceKey.isKey(it)) {
                list.add(PreferenceKey.deserialize(it))
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
        list.add(key)
        preference.edit().apply {
            val original = preference.getStringSet("keys", mutableSetOf())!!
            putStringSet("keys", original.plus(key.toString()))
            apply()
        }
    }

    /**
     * Remove a existing [Key].
     */
    fun remove(key: Key) {
        list.remove(key)
        val string = key.toString()
        preference.edit().apply {
            val original = preference.getStringSet("keys", mutableSetOf())!!
            original.remove(string)
            putStringSet("keys", original)
            apply()
        }
    }

    /**
     * Get a existing key with name.
     * @return The key, or null if it doesn't exist.
     */
    operator fun get(name: String) = keys.firstOrNull { it.name == name }
}