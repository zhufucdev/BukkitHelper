package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandFuture
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandResult
import com.zhufucdev.bukkit_helper.toByteArray
import com.zhufucdev.bukkit_helper.util.KeyringManager

class Login(private val key: Key, private val latency: Int, id: ByteArray) : ClientCommand(id) {
    override fun run(): CommandFuture {
        val registry = KeyringManager.keys.firstOrNull { it.matches(key, latency) }
            ?: // Not registered in keyring
            return CommandFuture.FORBIDEN
        // Parameters to return: Token & The token's survive time in ms
        val token = Server.getToken(registry.name)
        return CommandFuture {
            CommandResult(
                Respond.SUCCESS,
                token.bytes,
                token.timeDeath.toByteArray()
            )
        }
    }
}