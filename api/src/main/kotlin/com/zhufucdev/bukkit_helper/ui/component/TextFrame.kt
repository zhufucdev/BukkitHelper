package com.zhufucdev.bukkit_helper.ui.component

import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.Text

class TextFrame(text: Text): Component() {
    var text: Text = text
        set(value) {
            field = value
            redraw()
        }
}