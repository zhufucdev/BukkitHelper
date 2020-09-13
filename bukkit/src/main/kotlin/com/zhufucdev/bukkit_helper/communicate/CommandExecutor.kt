package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.CommonCommunication
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class CommandExecutor : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val result = (msg as ClientCommand).run()
        CommonCommunication.sendRespond(
            ctx,
            result.respond,
            *listOf(msg.id).plus(result.result).toTypedArray()
        )
    }
}