package com.zhufucdev.bukkithelper.communicate.handler

import android.util.Log
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkithelper.communicate.ServerCommand
import com.zhufucdev.bukkithelper.communicate.ServerTokenCommand
import com.zhufucdev.bukkithelper.communicate.TimeToken
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
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

        stack.add(msg)

        val request = msg.run()
        val out: ByteBuf
        if (msg is ServerTokenCommand<*>) {
            // If this command requires a token
            val token = token.get() ?: error("Token is null.")
            out = ctx.alloc().buffer(
                1 + token.bytes.size + request.pars.sumBy { it.size } + request.pars.size
            )

            CommonCommunication.writeRequest(
                out,
                request.command,
                token,
                msg.hashCode(),
                *request.pars.toTypedArray()
            )
        } else {
            out = ctx.alloc().buffer(1 + request.pars.sumBy { it.size } + request.pars.size)
            CommonCommunication.writeRequest(
                out,
                request.command,
                msg.hashCode(),
                *request.pars.toTypedArray()
            )
        }
        Log.d("${msg::class.simpleName}#${msg.hashCode()}","request = ${request.command.name}, pars = ${request.pars.joinToString { it.contentToString() }}")
        ctx.write(out, promise)
    }
}