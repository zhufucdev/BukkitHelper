package com.zhufucdev.bukkit_helper

import kotlin.random.Random

/**
 * A token is a temporary certificate that is held by client.
 */
open class Token {
    val holder: String
    val bytes: ByteArray
    /**
     * Construct a random token.
     */
    constructor(holder: String) {
        this.holder = holder
        bytes = Random.nextBytes(64)
    }

    /**
     * Construct a certain token with [holder] and [bytes].
     */
    constructor(holder: String, bytes: ByteArray) {
        this.holder = holder
        this.bytes = bytes
    }

    override fun equals(other: Any?): Boolean =
        other is Token && other.bytes.contentEquals(bytes) && other.holder == holder

    override fun hashCode(): Int {
        var result = holder.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}