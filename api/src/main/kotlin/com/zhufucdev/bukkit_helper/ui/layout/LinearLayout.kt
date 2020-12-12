package com.zhufucdev.bukkit_helper.ui.layout

import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.GroupComponent

class LinearLayout : GroupComponent {
    private var mVertical: Boolean
    private var mGravity: Gravity

    var isVertical: Boolean
        get() = mVertical
        set(value) {
            mVertical = value
            redraw()
        }

    var gravity: Gravity
        get() = mGravity
        set(value) {
            mGravity = value
            redraw()
        }

    constructor(
        children: List<Component>,
        vertical: Boolean = true,
        gravity: Gravity = Gravity.START,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT
    ) : super(children) {
        mVertical = vertical
        mGravity = gravity
        this.width = width
        this.height = height
    }

    constructor(
        vararg children: Component,
        vertical: Boolean = true,
        gravity: Gravity = Gravity.START,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT
    ) : this(children.toList(), vertical, gravity, width, height)

    enum class Gravity {
        START, END, CENTER, CENTER_VERTICAL, CENTER_HORIZONTAL
    }
}