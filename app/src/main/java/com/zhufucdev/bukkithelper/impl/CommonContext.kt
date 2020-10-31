package com.zhufucdev.bukkithelper.impl

import android.os.Build
import com.zhufucdev.bukkit_helper.Context
import com.zhufucdev.bukkit_helper.DynamicRefreshInterval
import com.zhufucdev.bukkithelper.manager.DataRefreshDelay
import java.util.*

object CommonContext : Context {
    private lateinit var context: android.content.Context
    fun init(context: android.content.Context) {
        this.context = context
        Context.setImplementation(this)
    }

    override fun getString(id: Int, vararg args: String): String = context.getString(id, *args)
    override val locale: Locale
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.resources.configuration.locales[0]
            else context.resources.configuration.locale

    override val dynamicRefreshInterval: DynamicRefreshInterval
        get() = DataRefreshDelay
}