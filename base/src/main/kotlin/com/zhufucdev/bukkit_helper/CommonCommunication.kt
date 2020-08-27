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
            if (input.readableBytes() < i * 4 + sum + 5) return null
            val bufLenThisPar = ByteArray(4)
            input.getBytes(i * 4 + sum + 1, bufLenThisPar, 0, 4)
            val lenThisPar = kotlin.run {
                var s = 0
                bufLenThisPar.forEachIndexed { index, byte ->
                    s += byte.toInt() shl (3 - index) * 8
                }
                s
            }
            // Read parameter
            if (input.readableBytes() < i * 4 + sum + lenThisPar + 1) return null
            val buffer = ByteArray(lenThisPar)
            input.getBytes(i * 4 + sum + 1, buffer, 0, lenThisPar)
            r.add(buffer)
            sum += lenThisPar
        }
        return r
    }

    private fun sendFormat(ctx: ChannelHandlerContext, header: Byte, pars: Array<out ByteArray>) {
        ctx.write(header)
        pars.forEach {
            // Write length
            val lenBuf = ByteArray(4)
            val len = it.size
            for (i in 0 until 4) {
                lenBuf[i] = (len shr 24 - i * 8).toByte()
            }
            ctx.write(lenBuf)
            // Write pars
            ctx.write(it)
        }
        ctx.flush()
    }

    /**
     * Encode and send the given command to [ctx].
     */
    fun sendRequest(ctx: ChannelHandlerContext, command: Command, vararg pars: ByteArray) {
        sendFormat(ctx, command.code, pars)
    }

    /**
     * Encode and send the given respond to [ctx].
     */
    fun sendRespond(ctx: ChannelHandlerContext, respond: Respond, vararg pars: ByteArray) {
        sendFormat(ctx, respond.code, pars)
    }
}