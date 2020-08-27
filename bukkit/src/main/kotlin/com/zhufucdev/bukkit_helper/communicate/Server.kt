package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.MainPlugin
import com.zhufucdev.bukkit_helper.Token
import io.netty.bootstrap.ServerBootstrap
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
    val options get() = listOf("port", "tokenSurvive")

    val tokens = arrayListOf<Token>()

    private var cFuture: ChannelFuture? = null
    val running: Boolean
        get() = cFuture != null

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
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                cFuture = b.bind(8080).sync()
            } finally {
                workerGroup.shutdownGracefully()
                bossGroup.shutdownGracefully()
            }
        }
    }

    fun stop() {
        cFuture?.channel()?.closeFuture()?.sync()
        cFuture = null
    }

    fun newToken(holder: String) = Token(holder).also {
        tokens.add(it)
        Bukkit.getScheduler().runTaskLater(MainPlugin.default, { _ ->

        }, 200)
    }
    fun hasToken(holder: String) = tokens.any { it.holder == holder }
    fun hasToken(content: ByteArray) = tokens.any { it.bytes.contentEquals(content) }
}