package com.zhufucdev.bukkit_helper.communicate

import java.lang.IllegalArgumentException
import java.net.SocketAddress

/**
 * Use [Server.Companion] to send commands.
 */
interface Server {
    fun sendCommand(command: ServerCommand<*>)
    fun sendCommandSync(command: ServerCommand<*>)
    val isConnected: Boolean
    val name: String
    val address: SocketAddress

    companion object : Server {
        private var mImpl: Server? = null
        private val impl get() = mImpl ?: error("API not ready.")
        fun setImplementation(impl: Server) {
            val validate = Thread.currentThread().stackTrace
                .any { it.className == "com.zhufucdev.bukkithelper.impl.CommonServer" }
            if (!validate)
                throw IllegalArgumentException("impl")
            mImpl = impl
        }

        /**
         * Check whether there is a connected server.
         */
        override val isConnected: Boolean
            get() = impl.isConnected

        /**
         * Get the name of the connected server.
         * @throws IllegalStateException If there is no server connected.
         */
        override val name: String
            get() = impl.name

        /**
         * Get the address of the connected server.
         * @throws IllegalStateException If there is no server connected.
         */
        override val address: SocketAddress
            get() = impl.address

        /**
         * Communicate with server and listen for result asynchronously.
         */
        override fun sendCommand(command: ServerCommand<*>) {
            impl.sendCommand(command)
        }

        /**
         * Communicate with server and wait for result synchronously.
         */
        override fun sendCommandSync(command: ServerCommand<*>) {
            impl.sendCommandSync(command)
        }
    }
}