package com.zhufucdev.bukkit_helper

import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.util.Translation
import com.zhufucdev.bukkit_helper.util.error
import com.zhufucdev.bukkit_helper.util.info
import com.zhufucdev.bukkit_helper.util.success
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ServerCommandExecutor : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val translation = Translation[sender]
        if (!sender.hasPermission("helper.server")) {
            sender.error(translation["server.error.permission"])
            return true
        }
        when (args.size) {
            0 -> sender.info(
                if (Server.running) translation["server.status.running", Server.port]
                else translation["server.status.stopped"]
            )
            1 -> {
                when (args[1]) {
                    "start" -> {
                        // <editor-fold desc="/server start">
                        if (Server.running) {
                            sender.error(translation["server.start.error.alreadyRunning"])
                            return true
                        }
                        try {
                            Server.run()
                            sender.success(translation["server.start.success", Server.port])
                        } catch (e: Exception) {
                            sender.error(translation["server.start.exception", e::class.simpleName.toString(), e.message.toString()])
                            e.printStackTrace()
                        }
                        // </editor-fold>
                    }
                    "stop" -> {
                        // <editor-fold desc="/server stop">
                        if (!Server.running) {
                            sender.error(translation["server.stop.error.notRunning"])
                            return true
                        }
                        try {
                            Server.stop()
                            sender.success(translation["server.stop.success"])
                        } catch (e: Exception) {
                            sender.error(translation["server.stop.exception", e::class.simpleName.toString(), e.message.toString()])
                            e.printStackTrace()
                        }
                        // </editor-fold>
                    }
                    "restart" -> {
                        // <editor-fold desc="/server restart"
                        try {
                            Server.stop()
                            Server.run()
                            sender.success(translation["server.restart.success", Server.port])
                        } catch (e: Exception) {
                            sender.error(translation["server.restart.error.exception", e::class.simpleName.toString(), e.message.toString()])
                        }
                        // </editor-fold>
                    }
                    "port" -> {
                        // <editor-fold desc="/server port">
                        if (args.size == 1) {
                            sender.info(translation["keyring.port.check", Server.port])
                        } else {
                            // <editor-fold desc="/server port <?> <...ignored>"
                            val port = args[1].toIntOrNull()
                            if (port == null || port <= 0) {
                                sender.error(translation["keyring.port.notInteger", args[1]])
                                return true
                            }
                            Server.port = port
                            // </editor-fold>
                        }
                        // </editor-fold>
                    }
                    else -> {
                        sender.error(translation["error.usage"])
                        return false
                    }
                }
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (args.size == 1) {
            return listOf("start", "stop", "restart", "port").filter { it.startsWith(args.first()) }.toMutableList()
        }
        return mutableListOf()
    }
}