package com.zhufucdev.bukkit_helper.util

import com.zhufucdev.bukkit_helper.MainPlugin
import com.zhufucdev.bukkit_helper.api.InfoCollect
import com.zhufucdev.bukkit_helper.player.DefaultPlayerInfo
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.nio.file.Paths
import java.util.*

object PlayerInfoManager : Listener {
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

        Bukkit.getPluginManager().registerEvents(this, MainPlugin.default)
    }

    operator fun get(uuid: UUID) =
        cache.firstOrNull { it.uuid == uuid } ?: DefaultPlayerInfo(Bukkit.getOfflinePlayer(uuid)).also { cache.add(it) }

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