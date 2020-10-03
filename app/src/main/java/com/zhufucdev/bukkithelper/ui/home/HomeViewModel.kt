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
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.communicate.LoginResult
import com.zhufucdev.bukkithelper.communicate.command.PlayerChangeListenCommand
import com.zhufucdev.bukkithelper.communicate.command.PlayerListCommand
import com.zhufucdev.bukkithelper.communicate.command.TPSFetchCommand
import com.zhufucdev.bukkithelper.communicate.listener.LoginListener
import com.zhufucdev.bukkithelper.manager.DataRefreshDelay
import com.zhufucdev.bukkithelper.manager.ServerManager
import com.zhufucdev.bukkithelper.ui.TPSValueFormatter
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread

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
                    handler.post {
                        updateInfo(result)
                    }
                }
            })
            addDisconnectListener {
                updateInfo(LoginResult.CONNECTION_FAILED)
            }
            connect()
        }
    }

    private var tpsTask: Timer? = null
    private fun cancelAllTasks() {
        tpsTask?.cancel()
    }

    var timeStart = System.currentTimeMillis()
        private set

    private fun updateInfo(login: LoginResult = LoginResult.SUCCESS) {
        cancelAllTasks()

        val server = ServerManager.connected
        if (server == null) {
            handler.post {
                _status.value = ConnectionStatus.DISCONNECTED
            }
            return
        }
        _connectionName.value = server.name
        _status.value = ConnectionStatus.CONNECTED

        if (login != LoginResult.SUCCESS) return // TODO: Handle Login Failure Better
        // <editor-fold desc="Dynamic">
        timeStart = System.currentTimeMillis()
        fun timeElapsed(): Long = System.currentTimeMillis() - timeStart
        // <editor-fold desc="TPS" defaultstate="collapsed">
        run {
            val tpsDataSet = arrayListOf<Entry>()
            fun getTpsData() = LineData(LineDataSet(tpsDataSet, context.getString(R.string.title_tick))).apply {
                setValueFormatter(TPSValueFormatter(context))
            }

            fun applyTpsTask() {
                tpsTask?.cancel()
                tpsTask =
                    fixedRateTimer(
                        name = "TPS-Task",
                        period = DataRefreshDelay[DataRefreshDelay.DataType.TPS].toLong()
                    ) {
                        val command = TPSFetchCommand()
                        command.addCompleteListener {
                            if (it == null) return@addCompleteListener
                            tpsDataSet.add(Entry(timeElapsed() / 1000F, it.toFloat()))
                            if (tpsDataSet.size >= 10) {
                                tpsDataSet.removeAt(0)
                            }
                            handler.post {
                                _tpsData.value = getTpsData()
                            }
                        }
                        server.channel.writeAndFlush(command)
                    }
            }
            applyTpsTask()
            // Listen changes
            DataRefreshDelay.addDelayChangeListener(DataRefreshDelay.DataType.TPS) { applyTpsTask() }
        }
        // </editor-fold>
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
                            playerDataSet.add(Entry((System.currentTimeMillis() - timeStart).toFloat(), it.size.toFloat()))
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

    override fun onCleared() {
        super.onCleared()
        cancelAllTasks()
    }

    private val _status = MutableLiveData<ConnectionStatus>()
    private val _connectionName = MutableLiveData<String>()
    private val _tpsData = MutableLiveData<LineData>()
    private val _playerData = MutableLiveData<LineData>()
    val connectionStatus: LiveData<ConnectionStatus> = _status
    val connectionName: LiveData<String> = _connectionName
    val tpsData: LiveData<LineData> = _tpsData
    val playerData: LiveData<LineData> = _playerData

    enum class ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED
    }
}