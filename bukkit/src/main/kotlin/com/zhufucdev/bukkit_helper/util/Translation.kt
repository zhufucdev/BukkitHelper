package com.zhufucdev.bukkit_helper.util

import com.zhufucdev.bukkit_helper.api.PlayerInfo
import com.zhufucdev.bukkit_helper.api.InfoCollect
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.util.*

object Translation {
    private val translations = hashMapOf<String, YamlConfiguration>()
    val DEFAULT_CODE: String

    init {
        fun load(name: String) =
            YamlConfiguration.loadConfiguration(
                (Translation::class.java.classLoader.getResourceAsStream("translation-$name.yml")
                    ?: error("Translation $name could not be found."))
                    .reader()
            )

        val default = load("default")
        DEFAULT_CODE = default.getString("info.name") ?: error("Default translation doesn't contain a code name.")
        translations[DEFAULT_CODE] = default

        // Load other translations
        default.getStringList("info.others").forEach {
            val load = load(it)
            if (!load.contains("info.code")) error("Translation $it doesn't contain a code name.")
            translations[load.getString("info.code")!!] = load
        }
    }

    fun get(code: String, path: String, vararg args: Any): String {
        val translation = translations[code] ?: throw IllegalArgumentException("Translation $code doesn't exist.")
        var str = translation.getString(path) ?: return ""
        var replaceIndex = 0
        var i = 0
        while (i < str.length - 1) {
            if (str[i] == '%' && str[i + 1] == 's') {
                var hasDigit = false
                val index =
                    if (i < str.length - 2 && str[i + 2].isDigit()) {
                        hasDigit = true
                        str[i + 2].toString().toInt()
                    } else
                        replaceIndex

                str = str.replaceRange(i..if (hasDigit) i + 2 else i + 1, args[index].toString())
                replaceIndex = index + 1
            }
            i ++
        }
        return str
    }

    fun getDefault(path: String, vararg args: Any) = get(DEFAULT_CODE, path, *args)

    operator fun get(code: String) = Getter(code)
    operator fun get(info: PlayerInfo) = Getter(info.preferredLanguage ?: DEFAULT_CODE)
    operator fun get(sender: CommandSender) = Getter(
        when (sender) {
            is Player -> InfoCollect[sender.uniqueId]?.preferredLanguage ?: DEFAULT_CODE
            is ConsoleCommandSender -> Locale.getDefault().language.let { if (translations.containsKey(it)) it else DEFAULT_CODE }
            else -> DEFAULT_CODE
        }
    )

    class Getter(val code: String) {
        operator fun get(path: String, vararg args: Any) = Translation.get(code, path, *args)
    }
}