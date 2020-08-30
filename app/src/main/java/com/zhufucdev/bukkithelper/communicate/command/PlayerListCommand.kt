package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.PlayerInfo
import com.zhufucdev.bukkit_helper.Token
import com.zhufucdev.bukkithelper.communicate.ServerCommand
import io.netty.buffer.ByteBuf

open class PlayerListCommand : ServerCommand<List<PlayerInfo>>() {
    override fun write(out: ByteBuf, token: Token) {
        CommonCommunication.writeRequest(out, Command.ONLINE_PLAYERS, token, hashCode())
    }
}