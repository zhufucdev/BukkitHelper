package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkit_helper.communicate.command.*
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * A decoder that transfers bytes into command.
 * In the specific way, the first byte indicates the type of command.
 * The next four ones indicate the first parameter's length, and the process goes on.
 */
class CommandDecoder : ByteToMessageDecoder() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        (msg as ByteBuf).readerIndex(0)
        super.channelRead(ctx, msg)
    }

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() < 1) return
        when (val command = Command.of(input.readByte())) {
            Command.LOGIN -> {
                val pars = CommonCommunication.parsePars(input, 2) ?: return
                out.add(Login(ctx, Key(pars.first()), pars[1].decodeToString().toInt()))
            }
            Command.TIME -> {
                out.add(ValidateTime(ctx))
            }
            else -> {
                // Other commands that require a token
                val token = CommonCommunication.parsePars(input, 1)?.first() ?: return
                if (!Server.hasToken(token)) {
                    out.add(ReturnForbidden(ctx))
                    return
                }
                when (command) {
                    Command.TPS -> {
                        val pars = CommonCommunication.parsePars(input, 1) ?: return
                        out.add(TestTPS(ctx, pars.first()))
                    }
                    Command.ONLINE_PLAYERS -> {
                        val pars = CommonCommunication.parsePars(input, 1) ?: return
                        out.add(OnlinePlayers(ctx, pars.first()))
                    }
                    Command.PLAYER_CHANGE -> {
                        val pars = CommonCommunication.parsePars(input, 1) ?: return
                        out.add(WaitPlayerChange(ctx, pars.first()))
                    }
                    else -> out.add(ReturnUnknown(ctx))
                }
            }
        }
    }
}