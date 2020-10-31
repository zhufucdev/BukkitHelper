package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.KnownCommand
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkit_helper.communicate.command.*
import com.zhufucdev.bukkit_helper.toInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * A decoder that transfers bytes into command.
 * In the specific way, the first byte indicates the type of command.
 * The next several ones indicate the parameters.
 * If the command is known as requiring a [TimeToken], the first parameter will be used.
 */
class CommandDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() < 1) return
        val command = KnownCommand.of(input.readByte())
        val id = CommonCommunication.parsePars(input, 1)?.first()
            ?: error("Command ${command.code} requires an ID but doesn't have one.")
        when (command) {
            KnownCommand.LOGIN -> {
                val pars = CommonCommunication.parsePars(input, 2) ?: return
                // Pars:
                // 1.Key
                // 2.Latency
                out.add(Login(Key(pars.first()), pars[1].toInt(), id))
            }
            KnownCommand.TIME -> {
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
                    KnownCommand.TPS -> {
                        out.add(TestTPS(id))
                    }
                    KnownCommand.ONLINE_PLAYERS -> {
                        out.add(OnlinePlayers(id))
                    }
                    KnownCommand.PLAYER_CHANGE -> {
                        out.add(WaitPlayerChange(id))
                    }
                    else -> out.add(ReturnUnknown(id))
                }
            }
        }
    }
}