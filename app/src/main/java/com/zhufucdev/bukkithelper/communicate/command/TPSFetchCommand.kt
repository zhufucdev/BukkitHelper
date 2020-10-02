package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.toDouble
import com.zhufucdev.bukkithelper.communicate.CommandRequest
import com.zhufucdev.bukkithelper.communicate.ServerCommand
import com.zhufucdev.bukkithelper.communicate.ServerTokenCommand
import io.netty.buffer.ByteBuf

class TPSFetchCommand : ServerTokenCommand<Double?>() {
    override fun run(): CommandRequest = CommandRequest(Command.TPS)

    override fun complete(data: ByteBuf) {
        val para = CommonCommunication.parsePars(data, 1)
            ?: error("Command TPSFetch requires 1 argument for result.")
        invokeComplete(para.first().toDouble())
    }
}