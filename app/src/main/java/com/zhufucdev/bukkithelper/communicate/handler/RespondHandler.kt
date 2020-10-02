package com.zhufucdev.bukkithelper.communicate.handler

import android.util.Log
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.toInt
import com.zhufucdev.bukkithelper.communicate.ServerCommand
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class RespondHandler(private val stack: ArrayList<ServerCommand<*>>) : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        // <editor-fold desc="Collect Info" defaultstate="collapsed">
        if ((msg as ByteBuf).readableBytes() < 1) return
        val respond = Respond.of(msg.readByte()) ?: error("Unknown respond: ${msg.getByte(0)}")
        val pars = CommonCommunication.parsePars(msg, 1) ?: error("id was not included in response.")
        val id = pars.first().toInt()
        val command = stack.firstOrNull { it.hashCode() == id }
            ?: error("command#$id was not found.")
        // </editor-fold>
        command.respond = respond
        kotlin.run {
            val data = ByteArray(msg.readableBytes())
            msg.getBytes(msg.readerIndex(), data)
            Log.d("${command::class.simpleName}#$id", "respond = ${respond.name}, data = ${data.contentToString()}")
        }
        if (respond == Respond.SUCCESS) {
            command.complete(msg)
        } else {
            command.failure(respond)
        }
        stack.remove(command)
    }
}