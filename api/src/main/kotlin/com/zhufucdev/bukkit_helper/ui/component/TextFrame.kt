package com.zhufucdev.bukkit_helper.ui.component

import com.zhufucdev.bukkit_helper.ui.Color
import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.Text

class TextFrame(text: Text, size: Int = 16, color: Color = DEFAULT_COLOR) : Component() {
    var text: Text = text
        set(value) {
            field = value
            redraw()
        }

    var size: Int = size
        set(value) {
            field = value
            redraw()
        }

    var color: Color = color
        set(value) {
            field = value
            redraw()
        }

    companion object {
        val DEFAULT_COLOR get() = Color(255, 120, 120, 120)
    }
}