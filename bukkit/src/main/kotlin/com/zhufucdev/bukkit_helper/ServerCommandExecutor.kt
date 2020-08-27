package com.zhufucdev.bukkit_helper

import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.util.Translation
import com.zhufucdev.bukkit_helper.util.error
import com.zhufucdev.bukkit_helper.util.info
import com.zhufucdev.bukkit_helper.util.success
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

class ServerCommandExecutor : TabExecutor {
    @Suppress("UNCHECKED_CAST")
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
                    "settings" -> {
                        fun checkProperty(): KMutableProperty1<Server, *>? {
                            val name = args[1]
                            val property = Server::class.memberProperties.firstOrNull { it.name == name }
                            if (!Server.options.contains(name) || property == null) {
                                sender.error(translation["server.settings.noSuchSettings", name])
                                return null
                            }
                            return property as KMutableProperty1<Server, *>
                        }
                        // <editor-fold desc="/server settings <...>">
                        when (args.size) {
                            1 -> {
                                // <editor-fold desc="/server settings">
                                sender.info(translation["server.settings.list", buildString {
                                    Server.options.forEach { append("$it ${translation["server.$it.name"]}, ") }
                                    delete(lastIndex - 1, length)
                                }])
                                // </editor-fold>
                            }
                            2 -> {
                                // <editor-fold desc="/server settings <?>"
                                val property = checkProperty() ?: return true
                                sender.info(translation["server.settings.check", property.get(Server).toString()])
                                // </editor-fold>
                            }
                            3 -> {
                                // <editor-fold desc="/server settings <?> <?>"
                                val property = checkProperty() ?: return true
                                when (val test = property.get(Server)) {
                                    is Int -> {
                                        val value = args[2].toIntOrNull()
                                        if (value == null || value < 0) {
                                            sender.error(translation["server.settings.notInteger", args[2]])
                                            return true
                                        }
                                        (property as KMutableProperty1<Server, Int>).set(Server, value)
                                    }
                                    is Long -> {
                                        val value = args[2].toLongOrNull()
                                        if (value == null || value < 0) {
                                            sender.error(translation["server.settings.notInteger", args[2]])
                                            return true
                                        }
                                        (property as KMutableProperty1<Server, Long>).set(Server, value)
                                    }
                                    is String -> {
                                        (property as KMutableProperty1<Server, String>).set(Server, args[2])
                                    }
                                    else -> {
                                        sender.error(translation["server.settings.unknown", test!!::class.simpleName.toString()])
                                    }
                                }
                            }
                            else -> sender.error(translation["error.usage"])
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
            return listOf("start", "stop", "restart", "settings").filter { it.startsWith(args.first()) }.toMutableList()
        }
        return mutableListOf()
    }
}