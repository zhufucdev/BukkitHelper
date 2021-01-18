package com.zhufucdev.bukkithelper.impl

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.GroupComponent
import com.zhufucdev.bukkit_helper.ui.component.Button
import com.zhufucdev.bukkit_helper.ui.component.TextEdit
import com.zhufucdev.bukkit_helper.ui.component.TextFrame
import com.zhufucdev.bukkit_helper.ui.data.Text
import com.zhufucdev.bukkit_helper.ui.layout.LinearLayout
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.android
import com.zhufucdev.bukkithelper.findViewWithType
import com.zhufucdev.bukkithelper.getMText
import com.zhufucdev.bukkithelper.ui.plugin_ui.UIHolder

/**
 * A utility that converts [Component] into desired [View].
 */
object UIParser {
    private fun parseLayoutParameters(view: View, component: Component) =
        if (view.layoutParams == null)
            ViewGroup.LayoutParams(component.width, component.height)
        else
            view.layoutParams.apply {
                width = component.width
                height = component.height
            }

    private fun <T : ViewGroup.LayoutParams> addChildren(
        component: GroupComponent,
        to: ViewGroup,
        paraClass: Class<T>,
        paraApply: T.(Component) -> Unit
    ) {
        component.children.forEach {
            val view = parse(it, to.context)
            val parameter = paraClass.getConstructor(ViewGroup.LayoutParams::class.java).newInstance(view.layoutParams)
            paraApply.invoke(parameter, it)
            view.layoutParams = parameter
            to.addView(view)
        }
    }

    private fun parseTextView(view: TextView, text: Text) {
        view.text = text.invoke()
        text.size?.run { view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, this.toFloat()) }
        text.color?.run { view.setTextColor(this.toARGB()) }
    }

    private fun parseLinearLayout(layout: android.widget.LinearLayout, component: LinearLayout) {
        layout.orientation =
            if (component.isVertical) android.widget.LinearLayout.VERTICAL
            else android.widget.LinearLayout.HORIZONTAL
        layout.gravity = component.gravity.android()
        addChildren(component, layout, android.widget.LinearLayout.LayoutParams::class.java) {
            component.layoutGravity[it]?.android()?.let { g ->
                this.gravity = g
            }
        }
    }

    private fun textWatcherFor(view: TextInputEditText, c: TextEdit) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            c.receive(mapOf("content" to view.getMText()))
        }
    }

    /**
     * Apply every setting required by [component] to a [view] of its type.
     */
    fun apply(view: View, component: Component) {
        when (component) {
            is TextFrame -> parseTextView(view as TextView, component.text)
            is Button -> {
                parseTextView(view as AppCompatButton, component.text)
                if (component.borderless) {
                    val ov = TypedValue()
                    view.context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, ov, true)
                    view.setBackgroundResource(ov.resourceId)
                }
            }
            is TextEdit -> {
                val input: TextInputEditText
                kotlin.run {
                    val index = (view as TextInputLayout).findViewWithType(TextInputEditText::class.java)
                    if (index == null) {
                        input = TextInputEditText(view.context)
                        view.addView(input)
                    } else {
                        input = index
                    }
                }
                parseTextView(input, component.content)
                (view as TextInputLayout).hint = component.hint.invoke()

                // Set up listener
                val last = input.getTag(R.id.text_edit_tag_last_primary_watcher) as TextWatcher?
                if (last != null)
                    input.removeTextChangedListener(last)
                val watcher = textWatcherFor(input, component)
                input.addTextChangedListener(watcher)
                input.setTag(R.id.text_edit_tag_last_primary_watcher, watcher)
            }
            is LinearLayout -> parseLinearLayout(view as android.widget.LinearLayout, component)
        }

        view.apply {
            layoutParams = parseLayoutParameters(this, component)
            tag = component
        }
    }

    fun parse(component: Component, context: Context): View {
        val view = when (component) {
            is TextFrame -> TextView(context)
            is Button -> AppCompatButton(context)
            is TextEdit -> TextInputLayout(context)
            is LinearLayout -> android.widget.LinearLayout(context)
            else -> {
                if (component::class != Component::class) {
                    throw UnsupportedOperationException("${component::class.qualifiedName} is not implemented.")
                }
                View(context)
            }
        }
        apply(view, component)
        return view
    }

    fun <T : View> findViewByComponent(component: Component): T = UIHolder[component.ui]?.findViewWithTag(component)
        ?: error("${component::class.simpleName}#${component.hashCode()} doesn't have View implementation.")
}