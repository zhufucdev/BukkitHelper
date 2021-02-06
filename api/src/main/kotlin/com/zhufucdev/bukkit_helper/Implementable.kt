package com.zhufucdev.bukkit_helper

import com.zhufucdev.bukkit_helper.workflow.Linkable

/**
 * Represents an API element, which is implemented internally.
 */
abstract class Implementable : Linkable {
    private val implListeners = arrayListOf<() -> Unit>()
    private var isImplemented = false

    fun addImplementedListener(l: () -> Unit) {
        if (implListeners.contains(l)) return
        if (isImplemented) l.invoke()
        implListeners.add(l)
    }

    fun removeImplementedListener(l: () -> Unit) {
        implListeners.remove(l)
    }

    open fun markImplemented() {
        isImplemented = true
        implListeners.forEach { it.invoke() }
    }

    open fun receive(data: Map<Any, Any?>) {}
}