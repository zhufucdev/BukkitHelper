package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkithelper.communicate.command.PlayerListCommand
import com.zhufucdev.bukkithelper.communicate.command.TPSFetchCommand
import com.zhufucdev.bukkithelper.minecraft.StaticPlayerInfo
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class RespondHandler(private val stack: ArrayList<ServerCommand<*>>) : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        // <editor-fold desc="Collect Info" defaultstate="collapsed">
        if ((msg as ByteBuf).readableBytes() < 1) return
        val respond = Respond.of(msg.readByte()) ?: error("Unknown respond: ${msg.getByte(0)}")
        val pars = CommonCommunication.parsePars(msg, 1) ?: return
        val id = pars.first().decodeToString().toIntOrNull() ?: error("id was not included in response.")
        val command = stack.firstOrNull { it.hashCode() == id }
            ?: error("command#$id was not found.")
        // </editor-fold>
        command.respond = respond
        when (respond) {
            Respond.SUCCESS -> {
                when (command) {
                    is TPSFetchCommand -> {
                        val para = CommonCommunication.parsePars(msg, 1) ?: return
                        command.invokeAll(para.first().decodeToString().toDouble())
                    }
                    is PlayerListCommand -> {
                        val size = msg.readInt()
                        val list = arrayListOf<StaticPlayerInfo>()
                        for (i in 0 until size) {
                            val info = CommonCommunication.parsePars(msg, 2) ?: return
                            list.add(
                                StaticPlayerInfo(
                                    name = info.first().decodeToString(),
                                    preferredLanguage = info[1].decodeToString().takeIf { it.isNotEmpty() }
                                )
                            )
                        }
                        command.invokeAll(list)
                    }
                }
            }
            Respond.FORBIDDEN -> {
                command.invokeAll(null)
            }
            else -> {
            }
        }
        stack.remove(command)
    }
}