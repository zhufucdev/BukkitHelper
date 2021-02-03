package com.zhufucdev.bukkit_helper.chart

/**
 * Represents a piece of data.
 */
class ChartElement {
    val x: Float
    val y: Float get() = mVals?.firstOrNull() ?: error("This element doesn't have Y property.")
    val hasY: Boolean get() = !mVals.isNullOrEmpty()
    private var mVals: ArrayList<Float>? = null
    val values: MutableList<Float>
        get() {
            if (mVals == null) mVals = arrayListOf()
            return mVals!!
        }
    val hasMutableValues: Boolean get() = mVals?.let { it.size >= 2 } == true

    private var mLabel: String? = null
    val label: String get() = mLabel.toString()
    val hasLabel: Boolean get() = mLabel != null

    constructor(x: Float) {
        this.x = x
    }

    constructor(x: Float, y: Float): this(x) {
        this.values.add(y)
    }

    constructor(x: Float, y: Float, label: String) : this(x, y) {
        this.mLabel = label
    }

    constructor(x: Float, vararg value: Float): this(x) {
        values.addAll(value.toTypedArray())
    }
}