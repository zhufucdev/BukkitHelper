package com.zhufucdev.bukkit_helper.ui.layout

import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.GroupComponent
import com.zhufucdev.bukkit_helper.ui.data.Gravity
import com.zhufucdev.bukkit_helper.ui.data.LayoutParameter
import com.zhufucdev.bukkit_helper.ui.data.Space

class LinearLayout : GroupComponent {
    var isVertical: Boolean
        set(value) {
            field = value
            redraw()
        }

    var gravity: Gravity
        set(value) {
            field = value
            redraw()
        }

    var padding: Space
        set(value) {
            field = value
            redraw()
        }

    val layoutGravity = LayoutParameter<Gravity>(this)
    val margin = LayoutParameter<Space>(this)

    constructor(
        children: List<Component>,
        vertical: Boolean = true,
        gravity: Gravity = Gravity.START,
        padding: Space = Space.EMPTY
    ) : super(children) {
        isVertical = vertical
        this.gravity = gravity
        this.padding = padding
    }

    constructor(
        vararg children: Component,
        vertical: Boolean = true,
        gravity: Gravity = Gravity.START,
        padding: Space = Space.EMPTY
    ) : this(children.toList(), vertical, gravity, padding)

}