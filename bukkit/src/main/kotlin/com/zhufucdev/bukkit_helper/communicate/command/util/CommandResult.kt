package com.zhufucdev.bukkit_helper.communicate.command.util

import com.zhufucdev.bukkit_helper.Respond

class CommandResult {
    val result: List<ByteArray>
    val respond: Respond
    constructor(respond: Respond, vararg result: ByteArray) {
        this.result = result.toList()
        this.respond = respond
    }
    constructor(respond: Respond) {
        this.respond = respond
        result = emptyList()
    }
}