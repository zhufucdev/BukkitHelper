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
        preference = PreferenceManager.getDefaultSharedPreferences(context)
        val strings = mutableSetOf<String>()
        preference.getStringSet("keys", strings)

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
        list.add(key)
        preference.edit().apply {
            val original = mutableSetOf<String>()
            preference.getStringSet("keys", original)
            putStringSet("keys", original.plus(key.toString()))
            apply()
        }
        return key
    }

    /**
     * Remove a existing [Key].
     */
    fun remove(key: Key) {
        list.remove(key)
        val string = key.toString()
        preference.edit().apply {
            val original = mutableSetOf<String>()
            preference.getStringSet("keys", original)
            original.remove(string)
            putStringSet("keys", original)
            apply()
        }
    }
}