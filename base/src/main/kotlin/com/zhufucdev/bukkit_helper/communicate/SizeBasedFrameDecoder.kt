package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.readIInt
import com.zhufucdev.bukkit_helper.toInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class SizeBasedFrameDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        if (msg.readableBytes() < 4) return

        // Move the index to target frame.
        val lastIndex = msg.readerIndex()
        msg.readerIndex(0)
        var frameSize = msg.readIInt()

        while (msg.readableBytes() >= 4 + frameSize && lastIndex !in msg.let { it.readerIndex() until it.readerIndex() + frameSize }) {
            msg.readerIndex(msg.readerIndex() + frameSize)
            frameSize = msg.readIInt()
        }
        // Start reading
        while (msg.readableBytes() >= frameSize) {
            out.add(msg.readBytes(frameSize))
            if (msg.readableBytes() < 4) {
                if (msg.readableBytes() == 0) break
                else error("Incomplete message.")
            }
            frameSize = msg.readIInt()
        }
    }
}