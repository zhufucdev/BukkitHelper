package com.zhufucdev.bukkit_helper.communicate

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class CommandExecutor : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        (msg as ClientCommand).run()
    }
}