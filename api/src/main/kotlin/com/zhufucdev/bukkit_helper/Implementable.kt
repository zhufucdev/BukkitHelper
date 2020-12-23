package com.zhufucdev.bukkit_helper

import com.zhufucdev.bukkit_helper.workflow.Linkable

abstract class Implementable : Linkable {
    private val implListeners = arrayListOf<() -> Unit>()
    private var isImplemented = false

    fun addImplementedListener(l: () -> Unit) {
        if (!isImplemented) {
            if (implListeners.contains(l)) return
            implListeners.add(l)
        } else {
            l.invoke()
        }
    }

    fun removeImplementedListener(l: () -> Unit) {
        implListeners.remove(l)
    }

    open fun markImplemented() {
        isImplemented = true
        implListeners.forEach { it.invoke() }
        implListeners.clear()
    }
}