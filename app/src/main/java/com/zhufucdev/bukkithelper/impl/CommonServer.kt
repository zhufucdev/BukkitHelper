package com.zhufucdev.bukkithelper.impl

import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.communicate.ServerCommand
import com.zhufucdev.bukkithelper.manager.ServerManager
import java.net.SocketAddress

object CommonServer : Server {
    fun init() {
        Server.setImplementation(this)
    }

    override val isConnected: Boolean
        get() = ServerManager.connected != null

    override val name: String
        get() = ServerManager.connected?.name ?: "null"

    override val address: SocketAddress
        get() = ServerManager.connected?.channel?.remoteAddress() ?: error("Server not connected.")

    override fun sendCommand(command: ServerCommand<*>) {
        ServerManager.connected?.channel?.writeAndFlush(command) ?: error("Server not connected.")
    }

    override fun sendCommandSync(command: ServerCommand<*>) {
        ServerManager.connected?.channel?.writeAndFlush(command)?.sync() ?: error("Server not connected.")
    }
}