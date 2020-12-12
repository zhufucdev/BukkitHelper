package com.zhufucdev.bukkithelper.ui.api_ui

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.GroupComponent
import com.zhufucdev.bukkit_helper.ui.component.TextFrame
import com.zhufucdev.bukkit_helper.ui.layout.LinearLayout
import com.zhufucdev.bukkit_helper.ui.layout.LinearLayout.Gravity.*

/**
 * A utility that converts [Component] into desired [View].
 */
object UIParser {
    fun parseLayoutParameters(component: Component) = ViewGroup.LayoutParams(component.width, component.height)

    fun addChildren(component: GroupComponent, to: ViewGroup) {
        component.children.forEach { to.addView(parse(it, to.context)) }
    }

    fun parse(component: Component, context: Context): View =
        when (component) {
            is TextFrame -> TextView(context).apply {
                text = component.text.invoke()
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, component.size.toFloat())
                setTextColor(component.color.toARGB())
            }
            is LinearLayout -> android.widget.LinearLayout(context).apply {
                orientation =
                    if (component.isVertical) android.widget.LinearLayout.VERTICAL
                    else android.widget.LinearLayout.HORIZONTAL
                gravity =
                    when (component.gravity) {
                        START -> Gravity.START
                        END -> Gravity.END
                        CENTER -> Gravity.CENTER
                        CENTER_VERTICAL -> Gravity.CENTER_VERTICAL
                        CENTER_HORIZONTAL -> Gravity.CENTER_HORIZONTAL
                    }
                addChildren(component, this)
            }
            else -> throw UnsupportedOperationException("${component::class.qualifiedName} is not implemented.")
        }.apply {
            layoutParams = parseLayoutParameters(component)
            tag = component
        }
}