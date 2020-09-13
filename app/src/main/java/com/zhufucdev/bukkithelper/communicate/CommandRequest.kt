package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.Command

class CommandRequest {
    val command: Command
    val pars: List<ByteArray>

    constructor(request: Command) {
        this.command = request
        this.pars = emptyList()
    }
    constructor(request: Command, vararg pars: ByteArray) {
        this.command = request
        this.pars = pars.toList()
    }
}