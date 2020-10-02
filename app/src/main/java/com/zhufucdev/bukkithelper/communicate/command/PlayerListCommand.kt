package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.api.PlayerInfo
import com.zhufucdev.bukkit_helper.readIInt
import com.zhufucdev.bukkithelper.communicate.CommandRequest
import com.zhufucdev.bukkithelper.communicate.ServerTokenCommand
import com.zhufucdev.bukkithelper.minecraft.StaticPlayerInfo
import io.netty.buffer.ByteBuf

open class PlayerListCommand : ServerTokenCommand<List<PlayerInfo>?>() {
    override fun run(): CommandRequest = CommandRequest(Command.ONLINE_PLAYERS)

    override fun complete(data: ByteBuf) {
        val size = data.readIInt()
        val list = arrayListOf<StaticPlayerInfo>()
        for (i in 0 until size) {
            val info = CommonCommunication.parsePars(data, 2) ?: return
            list.add(
                StaticPlayerInfo(
                    name = info.first().decodeToString(),
                    preferredLanguage = info[1].decodeToString().takeIf { it.isNotEmpty() }
                )
            )
        }
        invokeComplete(list)
    }
}