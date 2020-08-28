package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.communicate.command.Login
import com.zhufucdev.bukkit_helper.communicate.command.ValidateTime
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.net.SocketAddress

/**
 * A decoder that transfers bytes into command.
 * In the specific way, the first byte indicates the type of command.
 * The next four ones indicate the first parameter's length, and the process goes on.
 */
class CommandDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() < 1) return
        when (Command.of(input.readByte())) {
            Command.LOGIN -> {
                val pars = CommonCommunication.parsePars(input, 2) ?: return
                out.add(Login(ctx, Key(pars.first()), pars[1].decodeToString().toInt()))
            }
            Command.TIME -> {
                out.add(ValidateTime(ctx))
            }
        }
    }
}