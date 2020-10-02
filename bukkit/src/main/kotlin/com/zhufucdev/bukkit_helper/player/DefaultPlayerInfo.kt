package com.zhufucdev.bukkit_helper.player

import com.zhufucdev.bukkit_helper.api.PlayerInfo
import com.zhufucdev.bukkit_helper.util.PlayerInfoManager
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class DefaultPlayerInfo: PlayerInfo {
    val uuid: UUID
    private val tag: YamlConfiguration
    constructor(uuid: UUID, tag: YamlConfiguration) {
        this.uuid = uuid
        this.tag = tag
    }

    /**
     * Construct a new [DefaultPlayerInfo] according to the given [player].
     */
    constructor(player: OfflinePlayer) {
        this.uuid = player.uniqueId
        tag = YamlConfiguration()
    }

    override val name: String
        get() = bukkit.name!!

    override var preferredLanguage: String?
        get() = tag.getString("lang")
        set(value) {
            tag.set("lang", value)
        }

    val bukkit: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(uuid)

    fun save() {
        tag.save(File(PlayerInfoManager.configRoot, "$uuid.yml"))
    }
}