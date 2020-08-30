package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkithelper.communicate.listener.LoginListener
import com.zhufucdev.bukkithelper.manager.ServerManager
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import kotlin.concurrent.thread

class Server(val name: String, val host: String, val port: Int, val key: PreferenceKey) {
    private var token: TimeToken? = null
    val tokenHolding get() = token ?: error("Not login.")

    private val commandStack = arrayListOf<ServerCommand<*>>()

    private var cFuture: ChannelFuture? = null
    val channel: Channel get() = cFuture?.sync()?.channel() ?: error("Server not ready.")

    fun connect() {
        val onComplete: (LoginResult) -> Unit = { r ->
            loginListeners.forEach { it.invoke(r) }
        }

        if (ServerManager.connected == this) {
            onComplete(LoginResult.SUCCESS)
            return
        }
        ServerManager.disconnectCurrent()

        val workers = NioEventLoopGroup()
        val b = Bootstrap().apply {
            group(workers)
            channel(NioSocketChannel::class.java)
            option(ChannelOption.SO_KEEPALIVE, true)
            handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(TimeValidateHandler(onComplete), TokenFetchHandler(key, onComplete, ::token))
                        .addLast(CommandEncoder(commandStack, ::token), RespondHandler(commandStack))
                        .addLast(ExceptionHandler())
                }
            })
        }

        thread {
            try {
                val f = b.connect(host, port).sync()
                if (!f.isSuccess) onComplete(LoginResult.FAILED)
                cFuture = f
                ServerManager.connected = this
            } catch (e: Exception) {
                onComplete(LoginResult.FAILED)
            } finally {
                cFuture?.channel()?.closeFuture()?.sync()
                ServerManager.connected = null
                disconnectListeners.forEach { it.invoke() }
            }
        }
    }

    private val loginListeners = arrayListOf<LoginListener>()

    /**
     * Add a listener for the first attempt of login and every error following.
     * @param l The listener.
     */
    fun addLoginListener(l: LoginListener) {
        if (loginListeners.contains(l)) return
        loginListeners.add(l)
    }

    /**
     * Remove a listener for login.
     * @see addLoginListener
     */
    fun removeLoginListener(l: LoginListener) {
        loginListeners.remove(l)
    }

    /**
     * Remove all listeners for login.
     * @see addLoginListener
     */
    fun clearLoginListener() {
        loginListeners.clear()
    }

    private val disconnectListeners = arrayListOf<() -> Unit>()

    fun addDisconnectListener(l: () -> Unit) {
        disconnectListeners.add(l)
    }

    fun removeDisconnectListener(l: () -> Unit) {

    }

    fun disconnect() {
        val f = cFuture ?: error("Server is not connected.")
        f.channel().close().sync()
    }

    override fun equals(other: Any?): Boolean = other is Server
            && other.name == name && other.host == host && other.port == port && other.key == key

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + host.hashCode()
        result = 31 * result + port
        result = 31 * result + key.hashCode()
        return result
    }
}