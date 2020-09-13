package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandResult
import com.zhufucdev.bukkit_helper.toByteArray
import com.zhufucdev.bukkit_helper.util.KeyringManager
import io.netty.channel.ChannelHandlerContext

class Login(private val key: Key, private val latency: Int, id: ByteArray) : ClientCommand(id) {
    override fun run(): CommandResult {
        val registry = KeyringManager.keys.firstOrNull { it.matches(key, latency) }
            ?: // Not registered in keyring
            return CommandResult(Respond.FORBIDDEN)
        // Parameters to return: Token & The token's survive time in ms
        val token = Server.getToken(registry.name)
        return CommandResult(
            Respond.SUCCESS,
            token.bytes,
            token.timeDeath.toByteArray()
        )
    }
}