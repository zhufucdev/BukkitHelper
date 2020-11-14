package com.zhufucdev.bukkit_helper.ui.layout

import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.GroupComponent

class LinearLayout: GroupComponent {
    private var mVertical: Boolean

    var isVertical: Boolean
        get() = mVertical
        set(value) {
            mVertical = value
            redraw()
        }

    constructor(children: List<Component>, vertical: Boolean = true): super(children) {
        mVertical = vertical
    }

    constructor(vararg children: Component, vertical: Boolean = true): this(children.toList(), vertical)
}