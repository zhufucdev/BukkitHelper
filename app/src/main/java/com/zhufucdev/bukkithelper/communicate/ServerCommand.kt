package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.Respond
import com.zhufucdev.bukkit_helper.Token
import io.netty.buffer.ByteBuf

abstract class ServerCommand<T> {
    abstract fun write(out: ByteBuf, token: Token)

    private val listeners = arrayListOf<(T?) -> Unit>()
    var respond: Respond = Respond.CONTINUE
    fun addListener(l: (T?) -> Unit) {
        if (listeners.contains(l)) return
        listeners.add(l)
    }

    fun removeListener(l: (T?) -> Unit) {
        listeners.remove(l)
    }

    fun invokeAll(par: T?) {
        listeners.forEach { it.invoke(par) }
    }
}