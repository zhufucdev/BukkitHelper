package com.zhufucdev.bukkithelper.ui.home

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zhufucdev.bukkit_helper.DynamicList
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.plugin.ChartPlugin
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.communicate.LoginResult
import com.zhufucdev.bukkithelper.communicate.Server
import com.zhufucdev.bukkithelper.communicate.command.PlayerChangeListenCommand
import com.zhufucdev.bukkithelper.communicate.command.PlayerListCommand
import com.zhufucdev.bukkithelper.communicate.listener.LoginListener
import com.zhufucdev.bukkithelper.impl.AbstractPlugin
import com.zhufucdev.bukkithelper.manager.PluginManager
import com.zhufucdev.bukkithelper.manager.ServerManager
import com.zhufucdev.bukkithelper.ui.api_chart.ChartParser
import com.zhufucdev.bukkithelper.ui.api_chart.ChartViewAdapter

class HomeViewModel : ViewModel() {
    private val handler = Handler(Looper.getMainLooper())
    lateinit var context: Context

    fun connect() {
        if (ServerManager.connected == null && ServerManager.servers.isEmpty()) {
            updateInfo()
            return
        }
        _status.value = ConnectionStatus.CONNECTING
        ServerManager.default.apply {
            clearLoginListener()
            addLoginListener(object : LoginListener {
                override fun invoke(result: LoginResult) {
                    updateInfo(result)
                }
            })
            addDisconnectListener {
                updateInfo(LoginResult.CONNECTION_FAILED)
            }
            connect()
        }
    }

    var timeStart = System.currentTimeMillis()
        private set

    private var previousConnected: Server? = null
    private val charts = DynamicList<Chart>()
    private val pluginConcerned get() = PluginManager[ChartPlugin::class.java]
    private lateinit var chartViewAdapter: ChartViewAdapter
    private fun updateInfo(login: LoginResult = LoginResult.SUCCESS) {
        val server = ServerManager.connected

        // Main Concern: ChartPlugin

        if (server == null) {
            handler.post {
                _status.value = ConnectionStatus.DISCONNECTED
            }
            return
        }
        handler.post {
            _connectionName.value = server.name
            _status.value = ConnectionStatus.CONNECTED
        }

        if (previousConnected == server)
            return
        previousConnected = server

        if (login != LoginResult.SUCCESS) return // TODO: Handle Login Failure Better

        // <editor-fold desc="Draw charts">
        val collect = arrayListOf<Chart>()
        pluginConcerned.forEach {
            if (it.status < PluginManager.Status.AFTER_ENABLE) return@forEach
            val chart = (it.instance as ChartPlugin).chart
            if (!charts.contains(chart)) charts.add(chart)
            collect.add(chart)
        }
        charts.retainAll(collect)
        // Adapter
        if (!::chartViewAdapter.isInitialized) {
            chartViewAdapter = ChartViewAdapter(charts)
            _chartAdapter.postValue(chartViewAdapter)
        }

        // <editor-fold desc="List players" defaultstate="collapsed">
        run {
            val playerDataSet = arrayListOf<Entry>()
            fun getPlayerData() = LineData(LineDataSet(playerDataSet, context.getString(R.string.title_player_count)))
            fun notifyNextPlayerChange() {
                handler.post {
                    _playerData.value = getPlayerData()
                }
                server.channel.writeAndFlush(PlayerChangeListenCommand().apply {
                    addCompleteListener {
                        if (it != null) {
                            playerDataSet.add(
                                Entry(
                                    (System.currentTimeMillis() - timeStart).toFloat(),
                                    it.size.toFloat()
                                )
                            )
                            notifyNextPlayerChange()
                        }
                    }
                })
            }

            server.channel.writeAndFlush(PlayerListCommand().apply {
                addCompleteListener {
                    if (it != null) {
                        playerDataSet.add(Entry((System.currentTimeMillis() - timeStart).toFloat(), it.size.toFloat()))
                        notifyNextPlayerChange()
                    }
                }
            })
        }
        // </editor-fold>
        // </editor-fold>
    }

    private val _status = MutableLiveData<ConnectionStatus>()
    private val _connectionName = MutableLiveData<String>()
    private val _tpsData = MutableLiveData<LineData>()
    private val _playerData = MutableLiveData<LineData>()
    private val _chartAdapter = MutableLiveData<ChartViewAdapter>()
    val connectionStatus: LiveData<ConnectionStatus> = _status
    val connectionName: LiveData<String> = _connectionName
    val tpsData: LiveData<LineData> = _tpsData
    val playerData: LiveData<LineData> = _playerData
    val chartAdapter: LiveData<ChartViewAdapter> = _chartAdapter

    enum class ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED
    }
}