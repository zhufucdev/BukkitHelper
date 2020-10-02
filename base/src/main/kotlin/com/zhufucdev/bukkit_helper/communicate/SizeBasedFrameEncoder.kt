package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.toByteArray
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise

class SizeBasedFrameEncoder : ChannelOutboundHandlerAdapter() {
    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        if (msg !is ByteBuf) {
            error("${this::class.simpleName} should be placed at last of the pipeline.")
        }

        val buf = ctx.alloc().buffer(4 + msg.readableBytes())
        buf.writeBytes(msg.readableBytes().toByteArray())
        buf.writeBytes(msg)
        ctx.write(buf, promise)
    }
}