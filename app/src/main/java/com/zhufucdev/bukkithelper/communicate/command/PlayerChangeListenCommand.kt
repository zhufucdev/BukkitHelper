package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Token
import com.zhufucdev.bukkithelper.communicate.CommandRequest
import io.netty.buffer.ByteBuf

class PlayerChangeListenCommand : PlayerListCommand() {
    override fun run(): CommandRequest = CommandRequest(Command.PLAYER_CHANGE)
}