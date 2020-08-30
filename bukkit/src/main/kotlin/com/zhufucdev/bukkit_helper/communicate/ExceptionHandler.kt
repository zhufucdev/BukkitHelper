package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.MainPlugin
import com.zhufucdev.bukkit_helper.util.Translation
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class ExceptionHandler : ChannelInboundHandlerAdapter() {
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        MainPlugin.default.logger.warning(
            Translation.getDefault(
                "server.error.exception",
                ctx.channel().remoteAddress().toString()
            )
        )
        if (Server.debug) {
            cause.printStackTrace()
        } else {
            MainPlugin.default.logger.warning("${cause::class.simpleName}: ${cause.message}")
        }
    }
}