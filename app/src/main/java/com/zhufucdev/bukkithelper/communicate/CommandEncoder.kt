package com.zhufucdev.bukkithelper.communicate

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.jvm.isAccessible

class CommandEncoder(private val stack: ArrayList<ServerCommand<*>>, private val token: KMutableProperty0<TimeToken?>) : MessageToByteEncoder<ServerCommand<*>>() {
    init {
        token.isAccessible = true
    }
    override fun encode(ctx: ChannelHandlerContext, msg: ServerCommand<*>, out: ByteBuf) {
        val token = token.get() ?: error("Token is null.")
        msg.write(out, token)
        stack.add(msg)
    }
}