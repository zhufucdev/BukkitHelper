package com.zhufucdev.bukkit_helper

import com.zhufucdev.bukkit_helper.util.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class KeyringCommandExecutor : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val translation = Translation[sender]
        if (!sender.hasPermission("helper.keyring")) {
            sender.error(translation["keyring.error.permission"])
            return true
        }
        if (args.isEmpty()) {
            sender.error(translation["error.usage"])
            return false
        }
        // Start -- /keyring <?>
        when (args.first()) {
            "list" -> {
                // <editor-fold desc="/keyring list">
                if (KeyringManager.keys.isEmpty()) {
                    sender.info(translation["keyring.list.empty"])
                    return true
                }
                sender.info(translation["keyring.list.title"])
                buildString {
                    KeyringManager.keys.forEach { append("${it.name} $it, ") }
                    delete(lastIndex - 1, length)
                }.let {
                    sender.info(it)
                }
                // </editor-fold>
            }
            "add" -> {
                // <editor-fold desc="/keyring add">
                if (args.size < 3) {
                    sender.error(translation["error.usage"])
                    return true
                }
                // <editor-fold desc="/keyring add <?> <?> <...ignored>">
                val name = args[1]
                val content = args[2]
                if (!Key.isKey(content)) {
                    sender.error(translation["keyring.error.notKey", content])
                    return true
                }
                try {
                    KeyringManager.add(name, content)
                } catch (e: FileAlreadyExistsException) {
                    sender.error(translation["keyring.error.exists", name])
                    return true
                } catch (e: Exception) {
                    sender.error(translation["keyring.error.exception", e::class.simpleName.toString(), e.message.toString()])
                    e.printStackTrace()
                    return true
                }
                sender.success(translation["keyring.add.success", name])
                // </editor-fold>
                // </editor-fold>
            }
            "remove" -> {
                // <editor-fold desc="/keyring remove">
                if (args.size < 2) {
                    sender.error(translation["error.usage"])
                    return true
                }
                // <editor-fold desc="/keyring remove <?> <...ignored>"
                val name = args[1]
                if (!KeyringManager.remove(name)) {
                    sender.error(translation["keyring.error.noSuchKey", name])
                    return true
                }
                sender.success(translation["keyring.remove.success", name])
                // </editor-fold>
                // </editor-fold>
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
        if (!sender.hasPermission("helper.keyring"))
            return mutableListOf()
        val sub = mutableListOf("list", "add", "remove")
        // <editor-fold desc="/keyring"
        if (args.isEmpty())
            return sub
        // </editor-fold>
        when (args.size) {
            1 -> {
                // <editor-fold desc="/keyring <?>">
                return sub.filter { it.startsWith(args.first()) }.toMutableList()
                // </editor-fold>
            }
            2 -> {
                // <editor-fold desc="/keyring remove">
                if (args.first() == "remove")
                    return KeyringManager.keys.map { it.name }.filter { it.startsWith(args[1]) }.toMutableList()
                // </editor-fold>
            }
        }
        return mutableListOf()
    }
}