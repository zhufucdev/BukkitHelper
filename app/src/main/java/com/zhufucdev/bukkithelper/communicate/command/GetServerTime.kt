package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.KnownCommand
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.toLong
import com.zhufucdev.bukkit_helper.communicate.CommandRequest
import com.zhufucdev.bukkit_helper.communicate.ServerCommand
import io.netty.buffer.ByteBuf

class GetServerTime : ServerCommand<Long>() {
    var latency = -1L
    var timeStart = -1L
    override fun run(): CommandRequest {
        timeStart = System.currentTimeMillis()
        return CommandRequest(KnownCommand.TIME)
    }
    override fun complete(data: ByteBuf) {
        val end = System.currentTimeMillis()
        latency = (end - timeStart) / 2

        val pars = CommonCommunication.parsePars(data, 1)
            ?: error("Command GetServerTime requires 1 argument for result.")
        invokeComplete(pars.first().toLong())
    }
}