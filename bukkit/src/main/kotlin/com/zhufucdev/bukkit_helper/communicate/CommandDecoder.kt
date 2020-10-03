package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkit_helper.communicate.command.*
import com.zhufucdev.bukkit_helper.toInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.bukkit.Bukkit

/**
 * A decoder that transfers bytes into command.
 * In the specific way, the first byte indicates the type of command.
 * The next four ones indicate the first parameter's length, and the process goes on.
 */
class CommandDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() < 1) return
        val command = Command.of(input.readByte())
        val id = CommonCommunication.parsePars(input, 1)?.first()
            ?: error("Command ${command.name} requires an ID but doesn't have one.")
        when (command) {
            Command.LOGIN -> {
                val pars = CommonCommunication.parsePars(input, 2) ?: return
                // Pars:
                // 1.Key
                // 2.Latency
                out.add(Login(Key(pars.first()), pars[1].toInt(), id))
            }
            Command.TIME -> {
                out.add(ValidateTime(id))
            }
            else -> {
                // Other commands that require a token
                val token = CommonCommunication.parsePars(input, 1)?.first() ?: return
                if (!Server.hasToken(token)) {
                    out.add(ReturnForbidden(id))
                    return
                }
                when (command) {
                    Command.TPS -> {
                        out.add(TestTPS(id))
                    }
                    Command.ONLINE_PLAYERS -> {
                        out.add(OnlinePlayers(id))
                    }
                    Command.PLAYER_CHANGE -> {
                        out.add(WaitPlayerChange(id))
                    }
                    else -> out.add(ReturnUnknown(id))
                }
            }
        }
    }
}