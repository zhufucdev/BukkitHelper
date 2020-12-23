package com.zhufucdev.bukkit_helper.ui.component

import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.Text

class Button(text: Text, borderless: Boolean = false) : Component() {
    var text: Text = text
        set(value) {
            field = value
            redraw()
        }

    var borderless: Boolean = borderless
        set(value) {
            field = value
            redraw()
        }
}