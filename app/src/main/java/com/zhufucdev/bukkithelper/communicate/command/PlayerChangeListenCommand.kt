package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.KnownCommand
import com.zhufucdev.bukkit_helper.communicate.CommandRequest

class PlayerChangeListenCommand : PlayerListCommand() {
    override fun run(): CommandRequest = CommandRequest(KnownCommand.PLAYER_CHANGE)
}