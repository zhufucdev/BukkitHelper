package com.zhufucdev.bukkithelper.communicate.command

import com.zhufucdev.bukkit_helper.Command
import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.Token
import com.zhufucdev.bukkithelper.communicate.ServerCommand
import io.netty.buffer.ByteBuf

class TPSFetchCommand : ServerCommand<Double>() {
    override fun write(out: ByteBuf, token: Token) {
        CommonCommunication.writeRequest(out, Command.TPS, token, hashCode())
    }
}