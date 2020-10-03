package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.api.InfoCollect
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandFuture
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandResult
import com.zhufucdev.bukkit_helper.toByteArray
import org.bukkit.Bukkit

open class OnlinePlayers(id: ByteArray) : ClientCommand(id) {
    override fun run(): CommandFuture {
        return CommandFuture {
            val list = Bukkit.getOnlinePlayers().map { InfoCollect[it.uniqueId]!! }
            val r = arrayListOf<ByteArray>()
            r.add(list.size.toByteArray())
            list.forEach {
                r.add(it.name.toByteArray())
                r.add(it.preferredLanguage?.toByteArray() ?: byteArrayOf())
            }

            CommandResult(
                Respond.SUCCESS,
                *r.toTypedArray()
            )
        }
    }
}