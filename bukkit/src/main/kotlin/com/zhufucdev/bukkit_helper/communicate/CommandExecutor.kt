package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.toInt
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.bukkit.Bukkit

class CommandExecutor : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val result = (msg as ClientCommand).run()
        Bukkit.getLogger().info("Command#${msg.id.toInt()}: respond = ${result.respond.name}, data = ${result.result.joinToString { it.contentToString() }}")
        CommonCommunication.sendRespond(
            ctx,
            result.respond,
            *listOf(msg.id).plus(result.result).toTypedArray()
        )
    }
}