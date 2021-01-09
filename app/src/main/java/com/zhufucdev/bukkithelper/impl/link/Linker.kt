package com.zhufucdev.bukkithelper.impl.link

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.zhufucdev.bukkit_helper.ui.ChangedContent
import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.ComponentListener
import com.zhufucdev.bukkit_helper.ui.component.TextEdit
import com.zhufucdev.bukkit_helper.workflow.Executable
import com.zhufucdev.bukkit_helper.workflow.Execute
import com.zhufucdev.bukkit_helper.workflow.Navigatable
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.getMText
import com.zhufucdev.bukkithelper.impl.UIParser

/**
 * A utility that works with [CommonLink].
 */
object Linker {
    private fun secondaryWatcherFor(view: EditText, e: Executable<ChangedContent>): TextWatcher = object : TextWatcher {
        private var before: CharSequence? = null
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            before = s
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            val change = ChangedContent(
                before = view.getMText(replacement = before?.toString() ?: ""),
                after = view.getMText()
            )
            e.invoke(change)
        }
    }

    fun asExecute(e: Execute) {
        e.invoke(null)
    }

    fun listen(e: Executable<*>, from: Component) {
        from.addImplementedListener {
            when (e) {
                is TextEdit.ContentChanged -> {
                    val view = UIParser.findViewByComponent<TextInputLayout>(from)
                    view.editText?.apply {
                        val last = getTag(R.id.text_edit_tag_last_secondary_watcher) as TextWatcher?
                        if (last != null)
                            removeTextChangedListener(last)

                        val watcher = secondaryWatcherFor(this, e)
                        addTextChangedListener(watcher)
                        setTag(R.id.text_edit_tag_last_secondary_watcher, watcher)
                    }
                }
            }
        }
    }

    private fun checkOne(link: CommonLink, dest: Navigatable) {
        val from = link.from::class
        if (dest is ComponentListener) {
            if (link.from !is Component) {
                CommonLink.error(
                    link.from, dest, "staring with a ${from.simpleName} " +
                            "instead of a ${Component::class.simpleName}."
                )
            }
            if (dest::class.qualifiedName?.endsWith("${from.simpleName}.${dest::class.simpleName}") != true) {
                CommonLink.error(
                    link.from, dest, "${dest::class.simpleName} is not compatible with " +
                            from.simpleName
                )
            }
        }
    }

    fun checkCompatibility(link: CommonLink) {
        checkOne(link, link.primaryDestination)
        link.secondaryDestinations.forEach { checkOne(link, it) }
    }

    fun checkAndListen(link: CommonLink) {
        val action: (Navigatable) -> Unit = {
            checkOne(link, it)
            if (it is ComponentListener)
                listen(it as Executable<*>, link.from as Component)
        }
        action(link.primaryDestination)
        link.secondaryDestinations.forEach(action)
    }
}