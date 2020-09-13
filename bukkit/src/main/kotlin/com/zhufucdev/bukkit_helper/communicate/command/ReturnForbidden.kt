package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandResult
import io.netty.channel.ChannelHandlerContext

class ReturnForbidden(id: ByteArray) : ClientCommand(id) {
    override fun run(): CommandResult = CommandResult(Respond.FORBIDDEN)
}