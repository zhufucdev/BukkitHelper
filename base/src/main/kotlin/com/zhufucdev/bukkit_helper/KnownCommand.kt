package com.zhufucdev.bukkit_helper

import kotlin.reflect.KProperty

class KnownCommand(code: Byte, oneOnOne: Boolean = true): Command(code, oneOnOne) {
    companion object {
        fun of(code: Byte) = this::class.members.first { it is KProperty<*> && (it as KProperty<Command>).call(this).code == code }.call(this) as KnownCommand
        val LOGIN = KnownCommand(0, false)
        val TIME = KnownCommand(1, false)
        val TPS = KnownCommand(2)
        val ONLINE_PLAYERS = KnownCommand(3)
        val PLAYER_CHANGE = KnownCommand(4);
    }
}