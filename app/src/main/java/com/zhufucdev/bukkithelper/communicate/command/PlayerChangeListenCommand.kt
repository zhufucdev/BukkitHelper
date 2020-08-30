package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Token
import io.netty.buffer.ByteBuf

class PlayerChangeListenCommand : PlayerListCommand() {
    override fun write(out: ByteBuf, token: Token) {
        CommonCommunication.writeRequest(out, Command.PLAYER_CHANGE, token, hashCode())
    }
}