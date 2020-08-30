package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import io.netty.channel.ChannelHandlerContext

class ReturnUnknown(ctx: ChannelHandlerContext) : ClientCommand(ctx) {
    override fun run() {
        CommonCommunication.sendRespond(ctx, Respond.UNKNOWN_COMMAND)
    }
}