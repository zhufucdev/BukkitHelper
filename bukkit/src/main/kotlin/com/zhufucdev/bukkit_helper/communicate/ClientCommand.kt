package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.communicate.command.util.CommandFuture

abstract class ClientCommand(val id: ByteArray) {
    abstract fun run(): CommandFuture
}