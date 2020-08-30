package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.*
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.jvm.isAccessible

class TokenFetchHandler(
    private val key: Key,
    private val onComplete: (LoginResult) -> Unit,
    private val token: KMutableProperty0<TimeToken?>
) : ChannelInboundHandlerAdapter() {
    init {
        token.isAccessible = true
    }

    var success: Boolean = false
        private set

    override fun channelActive(ctx: ChannelHandlerContext) {
        val last = ctx.pipeline().get(TimeValidateHandler::class.java)
        if (!last.success) {
            // If the last handler failed
            // give up
            ctx.close()
        }
        if (success) {
            // If already login&token is alive
            val token = token.get()
            if (token != null && System.currentTimeMillis() < token.deathTime) {
                ctx.fireChannelActive()
            }
        }
        // Send login request
        CommonCommunication.sendRequest(
            ctx,
            Command.LOGIN,
            key.generateValidate(), // First par: the key
            last.latency.toString().toByteArray() // Second par: server latency
        )
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val t = token.get()
        if (t != null && System.currentTimeMillis() < t.deathTime) {
            ctx.fireChannelRead(msg)
            return
        }
        if ((msg as ByteBuf).readableBytes() < 1) return
        when (Respond.of(msg.readByte())) {
            Respond.SUCCESS -> {
                val args = CommonCommunication.parsePars(msg, 2) ?: return
                token.set(TimeToken(args.first(), args[1].decodeToString().toLong()))
                if (!success) onComplete(LoginResult.SUCCESS)
                success = true
                ctx.fireChannelActive()
            }
            Respond.FORBIDDEN -> {
                onComplete(LoginResult.FAILED)
                token.set(null)
                ctx.close()
            }
            else -> {
            }
        }
    }
}