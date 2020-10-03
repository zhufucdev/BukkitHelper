package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.MainPlugin
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandFuture
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class WaitPlayerChange(id: ByteArray) : OnlinePlayers(id), Listener {
    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        notifyPlayerChange()
    }
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        notifyPlayerChange()
    }

    private var changed = false
    override fun run(): CommandFuture {
        Bukkit.getPluginManager().registerEvents(this, MainPlugin.default)
        return CommandFuture {
            while (!changed) {
                Thread.sleep(400)
            }
            super.run().sync().result
        }
    }

    private fun notifyPlayerChange() {
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().runTaskLaterAsynchronously(MainPlugin.default, { _ ->
            changed = true
        }, 20)
    }
}