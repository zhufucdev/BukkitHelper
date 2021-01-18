package com.zhufucdev.bukkit_helper.ui.layout

import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.GroupComponent
import com.zhufucdev.bukkit_helper.ui.data.Gravity
import com.zhufucdev.bukkit_helper.ui.data.LayoutParameter

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

    val layoutGravity = LayoutParameter<Gravity>(this)

    constructor(
        children: List<Component>,
        vertical: Boolean = true,
        gravity: Gravity = Gravity.START
    ) : super(children) {
        mVertical = vertical
        mGravity = gravity
    }

    constructor(
        vararg children: Component,
        vertical: Boolean = true,
        gravity: Gravity = Gravity.START
    ) : this(children.toList(), vertical, gravity)

}