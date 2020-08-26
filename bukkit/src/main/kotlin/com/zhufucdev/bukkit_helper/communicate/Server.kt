package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.MainPlugin
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
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
    val handler = object : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {

        }
    }
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
                            ch.pipeline().addLast(handler)
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
}