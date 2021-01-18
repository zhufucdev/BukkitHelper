package com.zhufucdev.bukkit_helper.ui.component

import com.zhufucdev.bukkit_helper.ui.data.ChangedContent
import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.ComponentListener
import com.zhufucdev.bukkit_helper.ui.data.Text
import com.zhufucdev.bukkit_helper.workflow.Executable

class TextEdit(hint: Text = Text.EMPTY, initialText: Text = Text.EMPTY) : Component() {
    private var _content: Text = initialText
    var content: Text
        set(value) {
            _content = value
            redraw()
        }
        get() = _content

    var hint: Text = hint
        set(value) {
            field = value
            redraw()
        }

    override fun receive(data: Map<Any, Any?>) {
        val newContent = data["content"] as Text?
        if (newContent != null) _content = newContent
    }

    class ContentChanged(invoke: (ChangedContent) -> Unit) : Executable<ChangedContent>(invoke), ComponentListener
}