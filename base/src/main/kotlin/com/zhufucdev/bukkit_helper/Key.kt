package com.zhufucdev.bukkit_helper

import org.apache.commons.codec.binary.Base64
import java.security.MessageDigest
import java.util.*
import kotlin.random.Random

/**
 * A SHA-1 and time based key to validate the client's identity.
 */
open class Key {
    private val bytes: ByteArray

    /**
     * Constructs a random key using [Random] and SHA-1.
     */
    constructor() {
        val md = MessageDigest.getInstance("SHA-1")
        bytes = md.digest(Random.nextBytes(KEY_LENGTH))
    }

    /**
     * Constructs a certain key decoded via [Base64].
     */
    constructor(string: String) {
        bytes = Base64.decodeBase64(string)
    }

    /**
     * Constructs a certain key via [ByteArray].
     */
    constructor(bytes: ByteArray) {
        this.bytes = bytes
    }

    /**
     * Determine whether this key matches another.
     * @param other The key to match from client.
     * @param clientBehind Network latency in ms assuming server and client's clocks are exactly the same.
     */
    fun matches(other: Key, clientBehind: Int): Boolean {
        val calendar = Calendar.getInstance()
        // The other key should be sent this time ago.
        calendar.timeInMillis -= clientBehind
        // Ignore seconds and milliseconds
        calendar.time = Date()
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        // SHA-1 this key and the new Linux time
        val md = MessageDigest.getInstance("SHA-1")
        return md.digest(bytes.plus(calendar.timeInMillis.toString().toByteArray())).contentEquals(other.bytes)
    }

    /**
     * Generate a new byte array base on local time.
     * @return Bytes to be sent to server.
     */
    fun generateValidate(): ByteArray {
        val calendar = Calendar.getInstance()
        // Ignore seconds and milliseconds
        calendar.time = Date()
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        // SHA-1 this key and the new Linux time
        val md = MessageDigest.getInstance("SHA-1")
        return md.digest(bytes.plus(calendar.timeInMillis.toString().toByteArray()))
    }

    override fun toString(): String = Base64.encodeBase64String(bytes)

    override fun equals(other: Any?): Boolean = other is Key && other.bytes.contentEquals(bytes)

    override fun hashCode(): Int {
        return bytes.contentHashCode() * 31
    }

    companion object {
        const val KEY_LENGTH = 128
        const val KEY_BYTES_LENGTH = 20
        /**
         * Determine whether a [String] can construct a [Key].
         */
        fun isKey(content: String) = Base64.isBase64(content) && Base64.decodeBase64(content).size == KEY_BYTES_LENGTH
    }
}