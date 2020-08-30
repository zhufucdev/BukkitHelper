package com.zhufucdev.bukkit_helper

enum class Respond(val code: Byte) {
    SUCCESS(0), CONTINUE(1), UNKNOWN_COMMAND(2), FORBIDDEN(127);

    companion object {
        fun of(code: Byte) = values().firstOrNull { it.code == code }
    }
}