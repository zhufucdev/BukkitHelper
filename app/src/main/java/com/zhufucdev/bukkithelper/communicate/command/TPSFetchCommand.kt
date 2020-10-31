package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.KnownCommand
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.toDouble
import com.zhufucdev.bukkit_helper.communicate.CommandRequest
import com.zhufucdev.bukkit_helper.communicate.ServerTokenCommand
import io.netty.buffer.ByteBuf

class TPSFetchCommand : ServerTokenCommand<Double?>() {
    override fun run(): CommandRequest = CommandRequest(KnownCommand.TPS)

    override fun complete(data: ByteBuf) {
        val para = CommonCommunication.parsePars(data, 1)
            ?: error("Command TPSFetch requires 1 argument for result.")
        invokeComplete(para.first().toDouble())
    }
}