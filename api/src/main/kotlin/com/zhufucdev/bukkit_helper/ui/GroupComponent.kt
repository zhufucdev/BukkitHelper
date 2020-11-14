package com.zhufucdev.bukkit_helper.ui

abstract class GroupComponent(val children: List<Component>) : Component() {
    init {
        children.forEach {
            it.ui = ui
            it.parent = this
        }
    }
}