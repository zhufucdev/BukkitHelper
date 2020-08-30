package com.zhufucdev.bukkit_helper

enum class Command(val code: Byte, val oneOnOne: Boolean = true) {
    LOGIN(0, false), TIME(1, false), TPS(2), ONLINE_PLAYERS(3), PLAYER_CHANGE(4);

    companion object {
        fun of(code: Byte) = values().first { it.code == code }
    }
}