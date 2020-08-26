package com.zhufucdev.bukkit_helper.api

import com.zhufucdev.bukkit_helper.PlayerInfo
import java.util.*

object InfoCollect {
    private var method: ((UUID) -> PlayerInfo?)? = null
    fun setMethod(method: (UUID) -> PlayerInfo?) {
        this.method = method
    }
    private var defaultMethod: ((UUID) -> PlayerInfo?)? = null
    fun setDefaultMethod(method: (UUID) -> PlayerInfo?) {
        defaultMethod = method
    }

    operator fun get(uuid: UUID) = (method ?: defaultMethod ?: error("API not ready.")).invoke(uuid)
}