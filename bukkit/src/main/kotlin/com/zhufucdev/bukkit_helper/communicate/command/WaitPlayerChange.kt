package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.MainPlugin
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandResult
import io.netty.channel.ChannelHandlerContext
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
    override fun run(): CommandResult {
        Bukkit.getPluginManager().registerEvents(this, MainPlugin.default)
        while (!changed) {
            Thread.sleep(400)
        }
        return CommandResult(Respond.SUCCESS)
    }

    private fun notifyPlayerChange() {
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().runTaskLaterAsynchronously(MainPlugin.default, { _ ->
            changed = true
        }, 20)
    }
}