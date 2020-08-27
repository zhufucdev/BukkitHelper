package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.util.KeyringManager
import io.netty.channel.ChannelHandlerContext

class Login(ctx: ChannelHandlerContext, val key: Key, val latency: Int) : ClientCommand(ctx) {
    override fun run() {
        val registry = KeyringManager.keys.firstOrNull { it.matches(key, latency) }
        if (registry == null) {
            // Not registered in keyring
            CommonCommunication.sendRespond(ctx, Respond.FORBIDDEN)
            return
        }
        if (Server.hasToken(registry.name)) {
            // Already login
            CommonCommunication.sendRespond(ctx, Respond.DUPLICATED)
            return
        }
        CommonCommunication.sendRespond(ctx, Respond.SUCCESS, Server.newToken(registry.name).bytes)
    }
}