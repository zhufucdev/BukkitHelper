package com.zhufucdev.bukkit_helper.util

import com.zhufucdev.bukkit_helper.MainPlugin
import com.zhufucdev.bukkit_helper.communicate.FileKey
import java.io.File
import java.nio.file.Paths

object KeyringManager {
    val store = Paths.get("plugins", "BukkitHelper", "keyring").toFile()

    private val list = arrayListOf<FileKey>()
    val keys: List<FileKey> get() = list.toList()

    init {
        if (!store.exists()) store.mkdirs()
        else {
            store.listFiles()?.forEach { list.add(FileKey(it)) }
        }
    }

    /**
     * Add a specific key to local keyring.
     */
    fun add(name: String, content: String) {
        val file = File(store, name)
        if (list.any { it.name == name }) throw FileAlreadyExistsException(file)
        val key = FileKey(file, content)
        list.add(key)
        key.save()
    }

    /**
     * Remove a specific key.
     * @return true if the key is removed, false otherwise.
     */
    fun remove(name: String): Boolean = list.removeAll { it.name == name }

    @Synchronized
    fun saveAll() {
        list.forEach {
            try {
                it.save()
            } catch (e: Exception) {
                MainPlugin.default.logger.warning("Failed to save ${it.name} to local keyring:")
                e.printStackTrace()
            }
        }
    }
}