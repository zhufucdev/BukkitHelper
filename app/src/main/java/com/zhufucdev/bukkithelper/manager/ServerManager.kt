package com.zhufucdev.bukkithelper.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.zhufucdev.bukkithelper.communicate.LoginResult
import com.zhufucdev.bukkithelper.communicate.Server

object ServerManager {
    const val LOCAL_TOKEN_HOLDER = "local"

    private val list = arrayListOf<Server>()
    private lateinit var preference: SharedPreferences
    fun init(context: Context) {
        preference = context.getSharedPreferences("server_store", Context.MODE_PRIVATE)
        preference.all.forEach { (name, json) ->
            val obj = JsonParser.parseString(json as String).asJsonObject
            list.add(
                Server(
                    name,
                    obj["host"].asString,
                    obj["port"].asInt,
                    KeyManager[obj["key"].asString] ?: return@forEach
                )
            )
        }
    }

    private var connected: Server? = null
    val servers: List<Server> get() = list

    /**
     * Add the given [server] to server list of app, saving it to shared preference.
     */
    fun add(server: Server) {
        if (list.contains(server)) return
        list.add(server)
        preference.edit().apply {
            val obj = JsonObject()
            obj.addProperty("host", server.host)
            obj.addProperty("port", server.port)
            obj.addProperty("key", server.key.name)
            putString(server.name, obj.toString())
            apply()
        }
    }

    /**
     * Remove the given [server] from server list.
     */
    fun remove(server: Server) {
        list.remove(server)
    }

    /**
     * Add the [server] to server list and connect to it.
     */
    fun connect(server: Server, onComplete: (LoginResult) -> Unit) {
        add(server)
        server.connect(onComplete)
        connected = server
    }

    /**
     * Get a server with certain [name].
     */
    fun getByName(name: String) = list.firstOrNull { it.name == name }

    /**
     * Get a server with certain [address].
     */
    operator fun get(address: String) = list.firstOrNull { it.host == address }
}