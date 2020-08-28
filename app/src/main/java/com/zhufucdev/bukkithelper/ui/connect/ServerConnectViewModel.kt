package com.zhufucdev.bukkithelper.ui.connect

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zhufucdev.bukkithelper.communicate.LoginResult
import com.zhufucdev.bukkithelper.communicate.Server
import com.zhufucdev.bukkithelper.manager.ServerManager

class ServerConnectViewModel : ViewModel() {
    private val handler = Handler(Looper.getMainLooper())

    fun tryConnecting(server: Server, onComplete: (LoginResult) -> Unit) {
        _isConnecting.value = true
        ServerManager.connect(server) {
            handler.post {
                _isConnecting.value = false
                onComplete(it)
            }
        }
    }

    private val _isConnecting = MutableLiveData<Boolean>()
    val isConnecting: LiveData<Boolean> = _isConnecting
}