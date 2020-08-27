package com.zhufucdev.bukkit_helper.util

import io.netty.channel.ChannelHandlerContext
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

fun CommandSender.error(msg: String) {
    sendMessage(ChatColor.BOLD.toString() + ChatColor.RED.toString() + msg)
}

fun CommandSender.info(msg: String) {
    sendMessage(ChatColor.BOLD.toString() + ChatColor.GOLD.toString() + msg)
}

fun CommandSender.success(msg: String) {
    sendMessage(ChatColor.GREEN.toString() + msg)
}