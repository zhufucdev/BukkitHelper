package com.zhufucdev.bukkithelper.ui.connect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zhufucdev.bukkithelper.manager.KeyManager

class ServerConnectViewModel : ViewModel() {
    private val _keys = MutableLiveData<List<String>>().apply {
        value = KeyManager.keys.map {  }
    }
    val keys: LiveData<List<String>> = _keys
}