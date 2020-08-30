package com.zhufucdev.bukkithelper.communicate

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import io.netty.handler.codec.MessageToByteEncoder
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.jvm.isAccessible

class CommandEncoder(private val stack: ArrayList<ServerCommand<*>>, private val token: KMutableProperty0<TimeToken?>) :
    ChannelOutboundHandlerAdapter() {
    init {
        token.isAccessible = true
    }

    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        if (msg !is ServerCommand<*>) return

        val token = token.get() ?: error("Token is null.")
        val out = ctx.alloc().buffer()
        msg.write(out, token)
        stack.add(msg)

        ctx.write(out, promise)
    }
}