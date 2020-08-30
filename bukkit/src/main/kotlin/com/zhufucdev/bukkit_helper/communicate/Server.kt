package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.MainPlugin
import com.zhufucdev.bukkit_helper.Token
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.bukkit.Bukkit

object Server {
    var port: Int
        get() = MainPlugin.default.config.getInt("port", 8080)
        set(value) {
            MainPlugin.default.apply {
                config.set("port", value)
                saveConfig()
            }
        }
    var tokenSurvive: Long
        get() = MainPlugin.default.config.getLong("tokenSurvive", 15 * 60 * 20)
        set(value) {
            MainPlugin.default.apply {
                config.set("tokenSurvive", value)
                saveConfig()
            }
        }
    var autostart: Boolean
        get() = MainPlugin.default.config.getBoolean("autostart", true)
        set(value) {
            MainPlugin.default.apply {
                config.set("autostart", value)
                saveConfig()
            }
        }
    var debug: Boolean
        get() = MainPlugin.default.config.getBoolean("debug", false)
        set(value) {
            MainPlugin.default.apply {
                config.set("debug", value)
                saveConfig()
            }
        }
    val options get() = listOf("port", "tokenSurvive", "autostart", "debug")

    private val tokens = arrayListOf<TimeToken>()

    private var cFuture: ChannelFuture? = null
    val channel: Channel get() = cFuture?.channel() ?: error("Server not ready.")
    val running: Boolean
        get() = cFuture.let { it != null && it.channel().isActive }

    fun run() {
        Bukkit.getScheduler().runTaskAsynchronously(MainPlugin.default) { _ ->
            val bossGroup = NioEventLoopGroup()
            val workerGroup = NioEventLoopGroup()
            try {
                val b = ServerBootstrap()
                b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .childHandler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline()
                                .addLast(CommandDecoder())
                                .addLast(CommandExecutor())
                                .addLast(ExceptionHandler())
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                cFuture = b.bind(port).sync()

                cFuture!!.channel().closeFuture().sync()
            } finally {
                workerGroup.shutdownGracefully()
                bossGroup.shutdownGracefully()
            }
        }
    }

    fun stop() {
        cFuture?.channel()?.close()?.sync()
        cFuture = null
    }

    /**
     * Get a token for a specific [holder] from either generating if the [holder] doesn't have a token
     * or if the [holder]'s token is dead, or the list if not.
     */
    fun getToken(holder: String) =
        if (hasToken(holder)) tokens.first { it.holder == holder }
        else
            TimeToken(holder, System.currentTimeMillis() + tokenSurvive * 50).also {
                tokens.add(it)
                Bukkit.getScheduler().runTaskLater(MainPlugin.default, { _ ->
                    tokens.remove(it)
                }, tokenSurvive)
            }

    fun hasToken(holder: String) = tokens.any { it.holder == holder }
    fun hasToken(content: ByteArray) = tokens.any { it.bytes.contentEquals(content) }
}