package com.zhufucdev.bukkithelper.ui.api_ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.GroupComponent
import com.zhufucdev.bukkit_helper.ui.component.TextFrame
import com.zhufucdev.bukkit_helper.ui.layout.LinearLayout

/**
 * A utility that converts [Component] into desired [View].
 */
object UIParser {
    fun parseLayoutParameters(component: Component) = ViewGroup.LayoutParams(component.width, component.height)

    fun addChildren(component: GroupComponent, view: ViewGroup) {
        component.children.forEach { view.addView(parse(it, view.context)) }
    }

    fun parse(component: Component, context: Context): View =
        when (component) {
            is TextFrame -> TextView(context).apply {
                text = component.text.invoke()
            }
            is LinearLayout -> android.widget.LinearLayout(context).apply {
                orientation =
                    if (component.isVertical) android.widget.LinearLayout.VERTICAL
                    else android.widget.LinearLayout.HORIZONTAL
                addChildren(component, this)
            }
            else -> throw UnsupportedOperationException("${component::class.qualifiedName} is not implemented.")
        }.apply {
            layoutParams = parseLayoutParameters(component)
        }
}