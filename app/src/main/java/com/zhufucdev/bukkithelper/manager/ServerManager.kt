package com.zhufucdev.bukkithelper.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.zhufucdev.bukkithelper.communicate.LoginResult
import com.zhufucdev.bukkithelper.communicate.Server
import com.zhufucdev.bukkithelper.communicate.listener.LoginListener

object ServerManager {
    const val LOCAL_TOKEN_HOLDER = "local"

    private var mDefaultServer: Server? = null
    fun clearDefault() {
        val previous = mDefaultServer ?: return
        mDefaultServer = null
        // <editor-fold collapsed="true" desc="Save to preference">
        preference.edit().apply {
            val obj = JsonParser.parseString(preference.getString(previous.name, "")).asJsonObject
            obj.remove("default")
            putString(previous.name, obj.toString())
            apply()
        }
        // </editor-fold>
    }

    var default: Server
        get() = mDefaultServer ?: list.first()
        set(value) {
            mDefaultServer = value
            preference.edit().apply {
                if (!preference.contains(value.name)) {
                    add(value)
                } else {
                    val obj = JsonParser.parseString(preference.getString(value.name, "")).asJsonObject
                    obj.addProperty("default", true)
                    putString(value.name, obj.toString())
                }
                // <editor-fold desc="Overwrite existing defaults">
                preference.all.forEach { (s, any) ->
                    if (s == value.name) return@forEach
                    val obj = JsonParser.parseString(any as String).asJsonObject
                    if (obj.has("default")) {
                        obj.remove("default")
                        putString(s, obj.toString())
                    }
                }
                // </editor-fold>
                apply()
            }
        }

    private val list = arrayListOf<Server>()
    private lateinit var preference: SharedPreferences
    fun init(context: Context) {
        if (::preference.isInitialized) return

        preference = context.getSharedPreferences("server_store", Context.MODE_PRIVATE)
        preference.all.forEach { (name, json) ->
            val obj = JsonParser.parseString(json as String).asJsonObject
            val it = Server(
                name,
                obj["host"].asString,
                obj["port"].asInt,
                KeyManager[obj["key"].asString] ?: return@forEach
            )
            list.add(it)
            if (obj.has("default") && obj["default"].asBoolean) {
                // Set default
                mDefaultServer = it
            }
        }
    }

    var connected: Server? = null
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
            if (mDefaultServer == server) obj.addProperty("default", true)
            putString(server.name, obj.toString())
            apply()
        }
    }

    /**
     * Remove the given [server] from server list.
     */
    fun remove(server: Server) {
        list.remove(server)
        preference.edit().apply {
            remove(server.name)
            apply()
        }
        if (mDefaultServer == server) {
            mDefaultServer = null
        }
    }

    /**
     * Add the [server] to server list and connect to it.
     */
    fun connect(server: Server, onComplete: (LoginResult) -> Unit) {
        add(server)
        server.addLoginListener(
            object : LoginListener {
                override fun invoke(result: LoginResult) {
                    onComplete(result)
                    server.removeLoginListener(this)
                }
            }
        )
        server.connect()
        connected = server
        default = server
    }

    fun disconnectCurrent() {
        connected?.disconnect()
    }

    private val connectionListeners = arrayListOf<(Server) -> Unit>()
    private val disconnectionListeners = arrayListOf<(Server) -> Unit>()

    fun addConnectionListener(l: (Server) -> Unit) {
        if (!connectionListeners.contains(l))
            connectionListeners.add(l)
    }

    fun removeConnectionListener(l: (Server) -> Unit) {
        connectionListeners.remove(l)
    }

    fun addDisconnectionListener(l: (Server) -> Unit) {
        if (!disconnectionListeners.contains(l))
            disconnectionListeners.add(l)
    }

    fun removeDisconnectionListener(l: (Server) -> Unit) {
        disconnectionListeners.remove(l)
    }

    fun invokeConnection(server: Server) {
        connected = server
        connectionListeners.forEach { it.invoke(server) }
    }

    fun invokeDisconnection(server: Server) {
        if (connected == server) {
            connected = null
            disconnectionListeners.forEach { it.invoke(server) }
        }
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