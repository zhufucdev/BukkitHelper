package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class LatencyTestHandler(private val onComplete: (Int?, Channel) -> Unit) : ChannelInboundHandlerAdapter() {
    private var timeStart: Long = -1
    override fun channelActive(ctx: ChannelHandlerContext) {
        timeStart = System.currentTimeMillis()
        CommonCommunication.sendRequest(ctx, Command.TIME)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        onComplete((System.currentTimeMillis() - timeStart).toInt(), ctx.channel())
    }
}