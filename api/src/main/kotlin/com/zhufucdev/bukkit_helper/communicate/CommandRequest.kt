package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.Command

/**
 * Represent a command request to be handled directly in server's logic.
 */
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