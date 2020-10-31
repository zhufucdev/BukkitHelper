package com.zhufucdev.bukkit_helper.chart

/**
 * Represents a piece of data.
 */
class ChartElement {
    val x: Float
    private var mY: Float? = null
    val y: Float get() = mY ?: error("This element doesn't have Y property.")
    val hasY: Boolean get() = mY != null

    private var mLabel: String? = null
    val label: String get() = mLabel.toString()
    val hasLabel: Boolean get() = mLabel != null

    constructor(x: Float) {
        this.x = x
    }

    constructor(x: Float, y: Float): this(x) {
        this.mY = y
    }

    constructor(x: Float, y: Float, label: String) : this(x, y) {
        this.mLabel = label
    }
}