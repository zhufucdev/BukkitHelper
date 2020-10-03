package com.zhufucdev.bukkithelper.communicate

import android.util.Log
import com.zhufucdev.bukkit_helper.communicate.SizeBasedFrameDecoder
import com.zhufucdev.bukkit_helper.communicate.SizeBasedFrameEncoder
import com.zhufucdev.bukkithelper.communicate.command.GetServerTime
import com.zhufucdev.bukkithelper.communicate.command.Login
import com.zhufucdev.bukkithelper.communicate.handler.*
import com.zhufucdev.bukkithelper.communicate.listener.LoginListener
import com.zhufucdev.bukkithelper.manager.ServerManager
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LineBasedFrameDecoder
import kotlin.concurrent.thread
import kotlin.math.abs

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
                    ch.pipeline()
                        .addLast(
                            SizeBasedFrameEncoder(),
                            TokenOutboundHandler(this@Server),
                            CommandEncoder(commandStack, ::token)
                        )
                        .addLast(
                            SizeBasedFrameDecoder(),
                            RespondHandler(commandStack)
                        )
                        .addLast(ExceptionHandler())
                }
            })
        }

        thread {
            try {
                val f = b.connect(host, port).sync()
                if (!f.isSuccess) onComplete(LoginResult.CONNECTION_FAILED)
                else {
                    cFuture = f
                    ServerManager.connected = this
                    login()
                }
            } catch (e: Exception) {
                onComplete(LoginResult.FAILED)
            } finally {
                cFuture?.channel()?.closeFuture()?.sync()
                ServerManager.connected = null
                disconnectListeners.forEach { it.invoke() }
            }
        }
    }

    private fun login() {
        val onComplete: (LoginResult) -> Unit = { r ->
            loginListeners.forEach { it.invoke(r) }
        }

        fun doLogin(latency: Long) {
            Login(key, latency.toInt()).apply {
                addCompleteListener {
                    token = it.second
                    onComplete(it.first)
                }
                addFailureListener {
                    onComplete(LoginResult.FAILED)
                }
                channel.writeAndFlush(this).sync()
            }
        }

        GetServerTime().apply {
            addCompleteListener {
                if (abs(it - latency - timeStart) > 30000L)
                // When server and client argue time
                    onComplete(LoginResult.TIME)
                else
                    doLogin(latency)
            }
            addFailureListener {
                onComplete(LoginResult.FAILED)
            }
            channel.writeAndFlush(this).sync()
        }
    }

    /**
     * Test the network latency.
     * @param onComplete called when the operation succeeded or failed. Parameter is -2 when connection was timeout, or
     * the latency result in ms.
     */
    fun testLatency(onComplete: (Int) -> Unit): Channel {
        val workers = NioEventLoopGroup()
        val sandbox = arrayListOf<ServerCommand<*>>()
        val b = Bootstrap().apply {
            group(workers)
            channel(NioSocketChannel::class.java)
            option(ChannelOption.SO_KEEPALIVE, true)
            handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                        .addLast(SizeBasedFrameEncoder(), CommandEncoder(sandbox, ::token))
                        .addLast(SizeBasedFrameDecoder(), RespondHandler(sandbox))
                }
            })
        }
        val f = b.connect(host, port)
        f.addListener {
            if (!it.isSuccess) onComplete(-2)
        }
        f.channel().closeFuture().addListener {
            workers.shutdownGracefully()
        }
        val start = System.currentTimeMillis()
        GetServerTime().apply {
            addCompleteListener {
                onComplete((System.currentTimeMillis() - start).toInt())
                f.channel().close()
            }
            addFailureListener {
                onComplete(-1)
                f.channel().close()
            }
            f.sync().channel().writeAndFlush(this).sync()
        }
        return f.channel()
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

    /**
     * Add a listener for disconnection.
     */
    fun addDisconnectListener(l: () -> Unit) {
        disconnectListeners.add(l)
    }

    /**
     * Remove a listener for disconnection.
     */
    fun removeDisconnectListener(l: () -> Unit) {
        disconnectListeners.remove(l)
    }

    /**
     * Disconnect from the server.
     * @throws IllegalStateException if this server isn't connected.
     */
    fun disconnect() {
        val f = cFuture ?: error("Server is not connected.")
        f.channel().close().sync()
        token = null
        if (ServerManager.connected == this) ServerManager.connected = null
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