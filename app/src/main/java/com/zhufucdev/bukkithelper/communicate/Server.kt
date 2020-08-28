package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkit_helper.Token
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

class Server(val name: String, val host: String, val port: Int, val key: PreferenceKey) {
    private var token: TimeToken? = null
    val tokenHolding get() = token ?: error("Not login.")

    private var cFuture: ChannelFuture? = null
    fun connect(onComplete: (LoginResult) -> Unit) {
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
        cFuture = b.connect(host, port).addListener {
            if (!it.isSuccess) onComplete(LoginResult.CONNECTION_FAILED)
        }
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