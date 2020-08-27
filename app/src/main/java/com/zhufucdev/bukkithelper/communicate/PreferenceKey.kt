package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.Key
import org.apache.commons.codec.binary.Base64

/**
 * Represent a [Key] stored in SharedPreference.
 */
class PreferenceKey: Key {
    val name: String

    constructor(name: String): super() {
        this.name = name
    }

    constructor(name: String, bytes: ByteArray): super(bytes) {
        this.name = name
    }

    override fun toString(): String {
        return "$name: ${super.toString()}"
    }

    companion object {
        fun deserialize(text: String): PreferenceKey {
            val split = text.indexOf(": ")
            if (split == -1) throw IllegalArgumentException("text")
            return PreferenceKey(text.substring(0 until split), Base64.decodeBase64(text.substring(split + 2)))
        }

        fun isKey(content: String): Boolean {
            val split = content.indexOf(": ")
            if (split == -1) return false
            return Base64.decodeBase64(content.substring(split + 2)).size == KEY_LENGTH
        }
    }
}