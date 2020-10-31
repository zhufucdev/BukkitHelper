package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.Respond
import io.netty.buffer.ByteBuf

/**
 * Holder of a command, containing the request and result.
 * @param T Result of the command.
 */
abstract class ServerCommand<T> {
    abstract fun run(): CommandRequest
    abstract fun complete(data: ByteBuf)
    open fun failure(respond: Respond) {
        invokeFailure(respond)
    }

    private val listeners = arrayListOf<(T) -> Unit>()
    private val failureListeners = arrayListOf<(Respond) -> Unit>()
    var respond: Respond = Respond.CONTINUE
    fun addCompleteListener(l: (T) -> Unit) {
        if (listeners.contains(l)) return
        listeners.add(l)
    }

    fun addFailureListener(l: (Respond) -> Unit) {
        if (failureListeners.contains(l)) return
        failureListeners.add(l)
    }

    fun removeListener(l: (T) -> Unit) {
        listeners.remove(l)
    }

    fun removeFailureListener(l: (Respond) -> Unit) {
        failureListeners.remove(l)
    }

    protected fun invokeComplete(par: T) {
        listeners.forEach { it.invoke(par) }
    }

    protected fun invokeFailure(respond: Respond) {
        failureListeners.forEach { it.invoke(respond) }
    }
}