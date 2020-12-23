package com.zhufucdev.bukkithelper.ui.api_ui

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.GroupComponent
import com.zhufucdev.bukkit_helper.ui.Text
import com.zhufucdev.bukkit_helper.ui.component.Button
import com.zhufucdev.bukkit_helper.ui.component.TextFrame
import com.zhufucdev.bukkit_helper.ui.layout.LinearLayout
import com.zhufucdev.bukkit_helper.ui.layout.LinearLayout.Gravity.*
import com.zhufucdev.bukkithelper.R

/**
 * A utility that converts [Component] into desired [View].
 */
object UIParser {
    private fun parseLayoutParameters(component: Component) = ViewGroup.LayoutParams(component.width, component.height)

    private fun addChildren(component: GroupComponent, to: ViewGroup) {
        component.children.forEach { to.addView(parse(it, to.context)) }
    }

    private fun parseTextView(view: TextView, text: Text) {
        view.text = text.invoke()
        text.size?.run { view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, this.toFloat()) }
        text.color?.run { view.setTextColor(this.toARGB()) }
    }

    fun parse(component: Component, context: Context): View =
        when (component) {
            is TextFrame -> TextView(context).apply {
                parseTextView(this, component.text)
            }
            is Button -> AppCompatButton(context).apply {
                parseTextView(this, component.text)
                if (component.borderless) {
                    val ov = TypedValue()
                    context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, ov, true)
                    setBackgroundResource(ov.resourceId)
                }
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
            else -> {
                if (component::class != Component::class) {
                    throw UnsupportedOperationException("${component::class.qualifiedName} is not implemented.")
                }
                View(context)
            }
        }.apply {
            layoutParams = parseLayoutParameters(component)
            tag = component
        }
}