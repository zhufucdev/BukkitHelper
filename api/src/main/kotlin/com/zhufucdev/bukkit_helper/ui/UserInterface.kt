package com.zhufucdev.bukkit_helper.ui

import com.zhufucdev.bukkit_helper.Implementable

class UserInterface(override val label: Text, val rootComponent: Component) : Implementable() {
    init {
        rootComponent.ui = this
    }

    private var impl: ((Component) -> Boolean)? = null
    fun setRedrawImplementation(impl: (Component) -> Boolean) {
        val validate = Thread.currentThread().stackTrace
            .any { it.className == "com.zhufucdev.bukkithelper.ui.plugin_ui.PluginUIFragment" }
        if (!validate) throw IllegalArgumentException("impl")
        this.impl = impl
    }

    fun redraw(component: Component): Boolean {
        return impl?.invoke(component) ?: error("API not ready or this UserInterface has not been drawn.")
    }

    override fun markImplemented() {
        rootComponent.markImplemented()
        super.markImplemented()
    }
}