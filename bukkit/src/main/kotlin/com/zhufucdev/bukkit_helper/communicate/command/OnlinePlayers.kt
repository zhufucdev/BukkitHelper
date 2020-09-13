package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.api.InfoCollect
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandResult
import com.zhufucdev.bukkit_helper.toByteArray
import io.netty.channel.ChannelHandlerContext
import org.bukkit.Bukkit

open class OnlinePlayers(id: ByteArray) : ClientCommand(id) {
    override fun run(): CommandResult {
        val list = Bukkit.getOnlinePlayers().map { InfoCollect[it.uniqueId]!! }
        val r = arrayListOf<ByteArray>()
        r.add(id)
        r.add(list.size.toByteArray())
        list.forEach {
            r.add(it.name.toByteArray()) // 2.First par: name length
            r.add(it.preferredLanguage?.toByteArray() ?: byteArrayOf()) // 2.Second par: preferred language
        }
        return CommandResult(
            Respond.SUCCESS,
            *r.toTypedArray()
        )
    }
}