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
        CommonCommunication.sendRequest(ctx, Command.TIME)
        timeSent = System.currentTimeMillis()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if ((msg as ByteBuf).readableBytes() < 1) return

        when (Respond.of(msg.getByte(0))) {
            Respond.SUCCESS -> {
                val timeReceived = CommonCommunication.parsePars(msg, 1)?.first()?.toString()?.toLong() ?: return
                latency = (System.currentTimeMillis() - timeReceived).toInt() / 2
                if (abs(timeSent - timeReceived + latency) > 500L)
                    // When server and client argue time
                    onComplete(LoginResult.TIME)
                else
                    success = true
                ctx.close()
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