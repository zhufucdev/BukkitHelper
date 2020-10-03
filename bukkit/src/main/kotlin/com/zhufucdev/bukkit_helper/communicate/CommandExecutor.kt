package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.CommonCommunication
import com.zhufucdev.bukkit_helper.communicate.command.util.CommandResult
import com.zhufucdev.bukkit_helper.toInt
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.bukkit.Bukkit

class CommandExecutor : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val f = (msg as ClientCommand).run()
        f.setListener {
            if (it.isSuccess) {
                val result = it.result
                CommonCommunication.sendRespond(
                    ctx,
                    result.respond,
                    *listOf(msg.id).plus(result.result).toTypedArray()
                )
            }
        }
    }
}