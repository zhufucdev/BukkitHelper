package com.zhufucdev.bukkit_helper

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelHandlerContext

object CommonCommunication {
    const val SEPARATOR: Byte = 32
    const val TRANSLATOR: Byte = 64

    /**
     * Parses the [input].
     * @return a [List] contains all parameters, or null if the [input] is too short.
     */
    fun parsePars(input: ByteBuf, length: Int): List<ByteArray>? {
        val r = arrayListOf<ByteArray>()

        for (i in 0 until length) {
            if (input.readableBytes() < 1) return null

            val buf = ByteBufAllocator.DEFAULT.buffer(20)
            while (true) {
                val b = input.readByte()
                if (b == TRANSLATOR) {
                    if (input.readableBytes() < 1) buf.writeByte(b.toInt())
                    else buf.writeByte(input.readByte().toInt())
                } else if (b == SEPARATOR) {
                    break
                } else {
                    buf.writeByte(b.toInt())
                }
            }
            val array = ByteArray(buf.readableBytes())
            buf.readBytes(array)
            r.add(array)
        }
        return r
    }

    /**
     * Format data for [parsePars].
     */
    private fun writeFormat(buf: ByteBuf, header: Byte, pars: Array<out ByteArray>) {
        buf.writeByte(header.toInt())
        pars.forEach {
            // Write par
            it.forEach { b ->
                if (b == SEPARATOR || b == TRANSLATOR) buf.writeByte(TRANSLATOR.toInt())
                buf.writeByte(b.toInt())
            }
            // Write separator
            buf.writeByte(SEPARATOR.toInt())
        }
    }

    private fun sendFormat(ctx: ChannelHandlerContext, header: Byte, pars: Array<out ByteArray>) {
        val buf = ctx.alloc().buffer(1 + pars.sumBy { it.size } + pars.size * 4)
        writeFormat(buf, header, pars)
        ctx.writeAndFlush(buf).sync()
    }

    /**
     * Encode the given command.
     */
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