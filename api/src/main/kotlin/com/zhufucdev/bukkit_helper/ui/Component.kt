package com.zhufucdev.bukkit_helper.ui

import com.zhufucdev.bukkit_helper.Implementable
import com.zhufucdev.bukkit_helper.workflow.Linkable

open class Component : Implementable(), Linkable {
    var height: Int = WRAP_CONTENT
    var width: Int = WRAP_CONTENT

    override val label = Text(this::class.simpleName.toString())

    open var ui: UserInterface? = null
    var parent: GroupComponent? = null

    fun redraw(): Boolean {
        return ui?.redraw(this) == true
    }

    companion object {
        const val MATCH_PARENT = -1
        const val WRAP_CONTENT = -2
    }
}