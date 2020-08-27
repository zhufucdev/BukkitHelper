package com.zhufucdev.bukkit_helper

enum class Command(val code: Byte) {
    LOGIN(0), TIME(0);

    companion object {
        fun of(code: Byte) = values().first { it.code == code }
    }
}