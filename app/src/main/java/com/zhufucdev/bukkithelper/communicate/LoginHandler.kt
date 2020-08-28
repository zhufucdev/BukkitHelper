package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.*
import com.zhufucdev.bukkithelper.manager.ServerManager
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import kotlin.reflect.KMutableProperty0

class LoginHandler(
    private val key: Key,
    private val onComplete: (LoginResult) -> Unit,
    private val token: KMutableProperty0<TimeToken?>
) : ChannelInboundHandlerAdapter() {
    var success: Boolean = false
        private set
    private var sent = false

    override fun channelActive(ctx: ChannelHandlerContext) {
        val last = ctx.pipeline().get(TimeValidateHandler::class.java)
        if (!last.success) {
            // If the last handler failed
            // give up
            ctx.close()
        }
        if (sent) return

        CommonCommunication.sendRequest(
            ctx,
            Command.LOGIN,
            key.generateValidate(),
            last.latency.toString().toByteArray()
        )
        sent = true
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if ((msg as ByteBuf).readableBytes() < 1) return
        when (Respond.of(msg.readByte())) {
            Respond.SUCCESS -> {
                val args = CommonCommunication.parsePars(msg, 2) ?: return
                token.set(TimeToken(args.first(), args[1].decodeToString().toLong()))
                onComplete(LoginResult.SUCCESS)
                success = true
            }
            Respond.FORBIDDEN -> {
                onComplete(LoginResult.FAILED)
            }
            Respond.DUPLICATED -> {
                onComplete(LoginResult.SUCCESS)
                success = true
            }
            else -> { }
        }
    }
}