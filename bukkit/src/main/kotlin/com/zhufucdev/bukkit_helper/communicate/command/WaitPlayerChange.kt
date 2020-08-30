package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.MainPlugin
import io.netty.channel.ChannelHandlerContext
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class WaitPlayerChange(ctx: ChannelHandlerContext, id: ByteArray) : OnlinePlayers(ctx, id), Listener {
    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        notifyPlayerChange()
    }
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        notifyPlayerChange()
    }
    override fun run() {
        Bukkit.getPluginManager().registerEvents(this, MainPlugin.default)
    }

    private fun notifyPlayerChange() {
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().runTaskLaterAsynchronously(MainPlugin.default, { _ ->
            super.run()
        }, 20)
    }
}