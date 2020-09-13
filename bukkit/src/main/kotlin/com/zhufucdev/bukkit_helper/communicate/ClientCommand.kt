package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.communicate.command.util.CommandResult

abstract class ClientCommand(val id: ByteArray) {
    abstract fun run(): CommandResult
}