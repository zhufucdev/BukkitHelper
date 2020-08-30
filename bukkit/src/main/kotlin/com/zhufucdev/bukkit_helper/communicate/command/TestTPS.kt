package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import com.zhufucdev.bukkit_helper.util.TPSMonitor
import io.netty.channel.ChannelHandlerContext

class TestTPS(ctx: ChannelHandlerContext, private val id: ByteArray) : ClientCommand(ctx) {
    override fun run() {
        CommonCommunication.sendRespond(
            ctx,
            Respond.SUCCESS,
            id, // First par: command id
            TPSMonitor.value.toString().toByteArray() // Second par: TPS
        )
    }
}