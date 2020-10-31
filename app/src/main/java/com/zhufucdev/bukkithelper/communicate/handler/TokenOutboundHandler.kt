package com.zhufucdev.bukkithelper.communicate.handler

import com.zhufucdev.bukkit_helper.KnownCommand
import com.zhufucdev.bukkithelper.communicate.LoginResult
import com.zhufucdev.bukkithelper.communicate.Server
import com.zhufucdev.bukkit_helper.communicate.ServerTokenCommand
import com.zhufucdev.bukkithelper.communicate.command.GetServerTime
import com.zhufucdev.bukkithelper.communicate.command.Login
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise

class TokenOutboundHandler(private val parent: Server) : ChannelOutboundHandlerAdapter() {
    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        if (msg is ServerTokenCommand<*>) {
            val t = try {
                parent.tokenHolding
            } catch (e: Exception) {
                null
            }
            if (t == null || t.deathTime <= System.currentTimeMillis()) {
                // If the old key is invalidate or out-dated
                GetServerTime().apply {
                    addCompleteListener {
                        val command = Login(parent.key, latency.toInt())
                        command.addCompleteListener {
                            if (it.first == LoginResult.SUCCESS) {
                                ctx.writeAndFlush(msg, promise)
                            } else {
                                if (msg is KnownCommand)
                                    error("Failed to send command#${msg.hashCode()}: Login refused.")
                                else
                                    error("Failed to send data: Login refused.")
                            }
                        }
                        ctx.writeAndFlush(command)
                    }
                    addFailureListener {
                        error("Failed to send command#${msg.hashCode()}: Login refused.")
                    }

                    ctx.writeAndFlush(this)
                }

                return
            }
        }
        ctx.write(msg, promise)
    }
}