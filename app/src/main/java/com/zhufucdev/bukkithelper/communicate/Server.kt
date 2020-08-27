package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkit_helper.Token
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

class Server(val host: String, val port: Int) {
    private var token: Token? = null
    val tokenHolding get() = token ?: error("Not login.")

    private var cFuture: ChannelFuture? = null
    fun connect(key: Key, onComplete: (LoginResult) -> Unit) {
        val workers = NioEventLoopGroup()
        val b = Bootstrap().apply {
            group(workers)
            channel(NioSocketChannel::class.java)
            option(ChannelOption.SO_KEEPALIVE, true)
            handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(TimeValidateHandler(onComplete), LoginHandler(key, onComplete, ::token))
                }
            })
        }
        cFuture = b.connect(host, port).sync()
    }
}