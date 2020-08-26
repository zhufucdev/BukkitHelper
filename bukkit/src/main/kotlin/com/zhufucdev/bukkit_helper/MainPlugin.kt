package com.zhufucdev.bukkit_helper

import com.zhufucdev.bukkit_helper.api.InfoCollect
import com.zhufucdev.bukkit_helper.util.KeyringManager
import com.zhufucdev.bukkit_helper.util.PlayerInfoManager
import org.bukkit.plugin.java.JavaPlugin

class MainPlugin : JavaPlugin() {
    override fun onLoad() {
        default = this
    }

    override fun onEnable() {
        getCommand("keyring")!!.apply {
            val e = MainCommandExecutor()
            tabCompleter = e
            setExecutor(e)
        }

        InfoCollect.setDefaultMethod(PlayerInfoManager.generateIndexMethod())
    }

    override fun onDisable() {
        PlayerInfoManager.saveAll()
        KeyringManager.saveAll()
    }

    companion object {
        lateinit var default: MainPlugin
    }
}