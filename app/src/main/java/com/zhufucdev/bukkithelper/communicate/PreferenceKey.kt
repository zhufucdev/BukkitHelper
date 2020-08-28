package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.Key
import java.io.Serializable

/**
 * Represent a [Key] stored in SharedPreference.
 */
class PreferenceKey: Key, Serializable {
    val name: String

    constructor(name: String): super() {
        this.name = name
    }

    constructor(name: String, bytes: ByteArray): super(bytes) {
        this.name = name
    }

    constructor(name: String, content: String): super(content) {
        this.name = name
    }
}