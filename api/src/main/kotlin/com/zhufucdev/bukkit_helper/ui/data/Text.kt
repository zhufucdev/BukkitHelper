package com.zhufucdev.bukkit_helper.ui.data

import com.zhufucdev.bukkit_helper.Context

/**
 * Represents a readable visual content.
 */
class Text : () -> String {
    private val impl: () -> String

    /**
     * Text color.
     * App will try its best to apply this property.
     */
    val color: Color?

    /**
     * Text size in density pixel.
     * App will try its best to apply this property.
     */
    val size: Float?

    val isEmpty: Boolean
        get() = invoke().isEmpty()

    /**
     * Constructs a supplier text, which is convenient for user-triggered language change.
     *
     * Note that [color] and [size] selections may be ignored by availability.
     * @param size Text size in dp if possible.
     */
    constructor(supplier: () -> String, color: Color? = null, size: Float? = null) {
        impl = supplier
        this.color = color
        this.size = size
    }

    /**
     * Constructs a constant text.
     *
     * Note that [color] and [size] selection may be ignored by availability.
     * @param size Text size in dp if possible.
     */
    constructor(constant: String, color: Color? = null, size: Float? = null) : this({ constant }, color, size)

    /**
     * Constructs a resource text.
     *
     * Note that [color] and [size] selection may be ignored by availability.
     * @param size Text size in dp if possible.
     */
    constructor(resID: Int, color: Color? = null, size: Float? = null) : this({ Context.getString(resID) }, color, size)

    override fun invoke(): String = impl.invoke()

    fun format(color: Color? = null, size: Float? = null) = Text(impl, color ?: this.color, size ?: this.size)

    companion object {
        val EMPTY get() = Text("")
    }
}