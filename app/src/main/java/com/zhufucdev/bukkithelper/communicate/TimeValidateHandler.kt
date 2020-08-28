package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Respond
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import kotlin.math.abs

class TimeValidateHandler(private val onComplete: (LoginResult) -> Unit) :
    ChannelInboundHandlerAdapter() {
    var success: Boolean = false
        private set
    var latency = -1
    private var timeSent = 0L
    override fun channelActive(ctx: ChannelHandlerContext) {
        if (success) {
            ctx.fireChannelActive()
            return
        }
        CommonCommunication.sendRequest(ctx, Command.TIME)
        timeSent = System.currentTimeMillis()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (success) {
            ctx.fireChannelRead(msg)
            return
        }

        if ((msg as ByteBuf).readableBytes() < 1) return

        when (Respond.of(msg.readByte())) {
            Respond.SUCCESS -> {
                val timeReceived = CommonCommunication.parsePars(msg, 1)?.first()?.decodeToString()?.toLong() ?: return
                latency = (System.currentTimeMillis() - timeReceived).toInt() / 2
                if (abs(timeReceived - latency - timeSent) > 30000L)
                    // When server and client argue time
                    onComplete(LoginResult.TIME)
                else
                    success = true
                ctx.fireChannelActive()
            }
            else -> {
                onComplete(LoginResult.CONNECTION_FAILED)
                ctx.close()
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        onComplete(LoginResult.CONNECTION_FAILED)
        ctx.close()
    }
}