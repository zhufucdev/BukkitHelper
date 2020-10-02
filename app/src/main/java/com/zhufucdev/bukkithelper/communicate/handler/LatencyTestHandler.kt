package com.zhufucdev.bukkithelper.communicate.handler

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkithelper.communicate.command.GetServerTime
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class LatencyTestHandler(private val onComplete: (Int?, Channel) -> Unit) : ChannelInboundHandlerAdapter() {
    private var timeStart: Long = -1
    override fun channelActive(ctx: ChannelHandlerContext) {
        timeStart = System.currentTimeMillis()
        val command = GetServerTime().run()
        val buf = ctx.alloc().buffer()

        CommonCommunication.writeRequest(buf, command.command, command.hashCode(), *command.pars.toTypedArray())
        ctx.writeAndFlush(buf)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        onComplete((System.currentTimeMillis() - timeStart).toInt(), ctx.channel())
    }
}