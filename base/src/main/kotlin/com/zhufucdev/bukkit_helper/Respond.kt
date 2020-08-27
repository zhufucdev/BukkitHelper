package com.zhufucdev.bukkit_helper

enum class Respond(val code: Byte) {
    SUCCESS(0), DUPLICATED(1), CONTINUE(2), FORBIDDEN(127);

    companion object {
        fun of(code: Byte) = values().firstOrNull { it.code == code }
    }
}