package com.zhufucdev.bukkit_helper.ui

abstract class GroupComponent(val children: List<Component>) : Component() {
    init {
        children.forEach {
            it.ui = ui
            it.parent = this
        }
    }

    override var ui: UserInterface? = null
        set(value) {
            field = value
            children.forEach { it.ui = value }
        }

    override fun markImplemented() {
        children.forEach { it.markImplemented() }
        super.markImplemented()
    }
}