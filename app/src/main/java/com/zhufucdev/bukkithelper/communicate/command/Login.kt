package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.*
import com.zhufucdev.bukkit_helper.communicate.CommandRequest
import com.zhufucdev.bukkithelper.communicate.LoginResult
import com.zhufucdev.bukkit_helper.communicate.ServerCommand
import com.zhufucdev.bukkithelper.communicate.TimeToken
import io.netty.buffer.ByteBuf

class Login(private val key: Key, private val latency: Int) : ServerCommand<Pair<LoginResult, TimeToken?>>() {
    override fun run(): CommandRequest = CommandRequest(
        KnownCommand.LOGIN,
        key.generateValidate(), // First par: the key
        latency.toByteArray() // Second par: server latency
    )

    override fun complete(data: ByteBuf) {
        val pars = CommonCommunication.parsePars(data, 2)
            ?: error("Command Login requires 2 arguments for result.")
        val token = TimeToken(pars.first(), pars[1].toLong())
        invokeComplete(LoginResult.SUCCESS to token)
    }
}