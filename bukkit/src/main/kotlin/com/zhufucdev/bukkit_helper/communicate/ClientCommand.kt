package com.zhufucdev.bukkit_helper.communicate

import io.netty.channel.ChannelHandlerContext

abstract class ClientCommand(val ctx: ChannelHandlerContext): Runnable