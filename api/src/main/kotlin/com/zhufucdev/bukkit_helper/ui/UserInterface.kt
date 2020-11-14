package com.zhufucdev.bukkit_helper.ui

class UserInterface(val title: Text, val rootComponent: Component) {
    init {
        rootComponent.ui = this
    }

    private var impl: ((Component) -> Unit)? = null
    fun setRedrawImplementation(impl: (Component) -> Unit) {
        val validate = Thread.currentThread().stackTrace
            .any { it.className == "com.zhufucdev.bukkithelper.ui.plugin_ui.PluginUIFragment" }
        if (!validate) throw IllegalArgumentException("impl")

        this.impl = impl
    }

    fun redraw(component: Component) {
        impl?.invoke(component) ?: error("API not ready or this UserInterface has not been drawn.")
    }
}