package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.api.InfoCollect
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import io.netty.channel.ChannelHandlerContext
import org.bukkit.Bukkit

open class OnlinePlayers(ctx: ChannelHandlerContext, private val id: ByteArray) : ClientCommand(ctx) {
    override fun run() {
        val list = Bukkit.getOnlinePlayers().map { InfoCollect[it.uniqueId]!! }
        val buf = ctx.alloc().buffer()
        buf.writeByte(Respond.SUCCESS.code.toInt()) // 1.First par: respond
        buf.writeInt(id.size) // 1.Second par: id length
        buf.writeBytes(id) // 1.Third par: id
        buf.writeInt(list.size) // 1.Forth par: length
        list.forEach {
            buf.writeInt(it.name.length) // 2.First par: name length
            buf.writeBytes(it.name.toByteArray()) // 2.Second par: name content
            val lang = it.preferredLanguage
            buf.writeInt(lang?.length ?: 0) // 2.Third par: preferred language length
            if (lang != null) buf.writeBytes(lang.toByteArray()) // 2.Forth par: preferred language content
        }
        ctx.writeAndFlush(buf)
    }
}