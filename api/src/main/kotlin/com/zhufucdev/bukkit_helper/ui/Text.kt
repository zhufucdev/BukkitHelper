package com.zhufucdev.bukkit_helper.ui

import com.zhufucdev.bukkit_helper.Context

/**
 * Represents a readable visual content.
 */
class Text: () -> String {
    private val impl: () -> String
    val color: Color?
    val size: Int?

    val isEmpty: Boolean
        get() = invoke().isEmpty()

    /**
     * Constructs a supplier text, which is convenient for user-triggered language change.
     *
     * Note that [color] and [size] selections may be ignored by availability.
     * @param size Text size in dp if possible.
     */
    constructor(supplier: () -> String, color: Color? = null, size: Int? = null) {
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
    constructor(constant: String, color: Color? = null, size: Int? = null) {
        impl = { constant }
        this.color = color
        this.size = size
    }

    /**
     * Constructs a resource text.
     *
     * Note that [color] and [size] selection may be ignored by availability.
     * @param size Text size in dp if possible.
     */
    constructor(resID: Int, color: Color? = null, size: Int? = null) {
        impl = { Context.getString(resID) }
        this.color = color
        this.size = size
    }

    override fun invoke(): String = impl.invoke()

    fun format(color: Color? = null, size: Int? = null) = Text(impl, color ?: this.color, size ?: this.size)

    companion object {
        val EMPTY get() = Text("")
    }
}