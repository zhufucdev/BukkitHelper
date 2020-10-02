package com.zhufucdev.bukkit_helper.communicate.command

import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.communicate.ClientCommand
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandResult
import com.zhufucdev.bukkit_helper.toByteArray
import com.zhufucdev.bukkit_helper.util.TPSMonitor

class TestTPS(id: ByteArray) : ClientCommand(id) {
    override fun run(): CommandResult = CommandResult(
        Respond.SUCCESS,
        TPSMonitor.value.toByteArray() // Par: TPS
    )
}