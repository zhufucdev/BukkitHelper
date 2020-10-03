package com.zhufucdev.bukkit_helper.communicate.command.util

import com.zhufucdev.bukkit_helper.Respond
import kotlin.concurrent.thread

class CommandFuture(private val block: () -> CommandResult) {
    private var mResult: CommandResult? = null
    var isComplete: Boolean = false
        private set
    var isSuccess: Boolean = false
        private set
    var exception: Exception? = null
        private set
    val result: CommandResult
        get() {
            return mResult ?: error("Future failed or not completed.")
        }
    private val thread: Thread = thread {
        try {
            mResult = block.invoke()
            isSuccess = true
        } catch (e: Exception) {
            exception = e
        }
        isComplete = true
        mListener?.invoke(this)
    }
    private var mListener: ((CommandFuture) -> Unit)? = null
    fun setListener(l: (CommandFuture) -> Unit) {
        mListener = l

        if (isComplete) {
            l.invoke(this)
        }
    }

    fun sync(): CommandFuture {
        thread.join()
        return this
    }

    companion object {
        val FORBIDEN = CommandFuture { CommandResult(Respond.FORBIDDEN) }
        val UNKNOWN = CommandFuture { CommandResult(Respond.UNKNOWN_COMMAND) }
    }
}