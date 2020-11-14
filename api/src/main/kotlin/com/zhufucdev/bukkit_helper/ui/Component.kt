package com.zhufucdev.bukkit_helper.ui

open class Component {
    var height: Int = WRAP_CONTENT
    var width: Int = WRAP_CONTENT

    var ui: UserInterface? = null
    var parent: GroupComponent? = null

    fun redraw() {
        ui?.redraw(this)
    }

    companion object {
        const val WRAP_CONTENT = -2
        const val MATCH_PARENT = -1
    }
}