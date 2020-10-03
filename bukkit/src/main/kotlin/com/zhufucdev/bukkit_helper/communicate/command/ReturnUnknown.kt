package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandFuture

class ReturnUnknown(id: ByteArray) : ClientCommand(id) {
    override fun run(): CommandFuture = CommandFuture.UNKNOWN
}