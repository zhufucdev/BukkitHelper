package com.zhufucdev.bukkit_helper.util

import com.zhufucdev.bukkit_helper.MainPlugin
import com.zhufucdev.bukkit_helper.player.DefaultPlayerInfo
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Paths
import java.util.*

object PlayerInfoManager {
    val configRoot: File = Paths.get("plugins", "BukkitHelper", "players").toFile()
    private val cache = arrayListOf<DefaultPlayerInfo>()

    init {
        if (!configRoot.exists()) configRoot.mkdirs()
        else {
            configRoot.listFiles()!!.forEach {
                val tag = YamlConfiguration.loadConfiguration(it)
                val uuid = UUID.fromString(it.nameWithoutExtension)
                cache.add(DefaultPlayerInfo(uuid, tag))
            }
        }
    }

    operator fun get(uuid: UUID) = cache.firstOrNull { it.uuid == uuid }

    fun generateIndexMethod(): (UUID) -> DefaultPlayerInfo? = { get(it) }

    @Synchronized
    fun saveAll() {
        cache.forEach {
            try {
                it.save()
            } catch (e: Exception) {
                MainPlugin.default.logger.warning("Failed to save ${it.name}'s info:")
                e.printStackTrace()
            }
        }
    }
}