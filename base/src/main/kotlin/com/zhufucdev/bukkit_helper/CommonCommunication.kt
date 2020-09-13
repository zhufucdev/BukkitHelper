package com.zhufucdev.bukkit_helper

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

object CommonCommunication {
    /**
     * Parses the [input].
     * @return a [List] contains all parameters, or null if the [input] is too short.
     */
    fun parsePars(input: ByteBuf, length: Int): List<ByteArray>? {
        var sum = 0 // length of all parameters read
        val r = arrayListOf<ByteArray>()
        for (i in 0 until length) {
            if (input.readableBytes() < 4) return null
            val len = input.readInt()
            if (input.readableBytes() < len) return null
            val buf = ByteArray(len)
            input.readBytes(buf, 0, len)
            r.add(buf)

            sum += len
        }
        return r
    }

    private fun writeFormat(buf: ByteBuf, header: Byte, pars: Array<out ByteArray>) {
        buf.writeByte(header.toInt())
        pars.forEach {
            // Write length
            buf.writeInt(it.size)
            // Write par
            buf.writeBytes(it)
        }
    }

    private fun sendFormat(ctx: ChannelHandlerContext, header: Byte, pars: Array<out ByteArray>) {
        val buf = ctx.alloc().buffer(1 + pars.sumBy { it.size } + pars.size * 4)
        writeFormat(buf, header, pars)
        ctx.writeAndFlush(buf).sync()
    }

    /**
     * Encode and send the given command to [ctx].
     */
    fun sendRequest(ctx: ChannelHandlerContext, command: Command, vararg pars: ByteArray) {
        sendFormat(ctx, command.code, pars)
    }

    fun writeRequest(buf: ByteBuf, command: Command, id: Int, vararg pars: ByteArray) {
        writeFormat(buf, command.code, arrayOf(id.toByteArray()).plus(pars))
    }

    fun writeRequest(buf: ByteBuf, command: Command, token: Token, id: Int, vararg pars: ByteArray) {
        writeFormat(buf, command.code, arrayOf(id.toByteArray(), token.bytes).plus(pars))
    }

    /**
     * Encode and send the given respond to [ctx].
     */
    fun sendRespond(ctx: ChannelHandlerContext, respond: Respond, vararg pars: ByteArray) {
        sendFormat(ctx, respond.code, pars)
    }
}