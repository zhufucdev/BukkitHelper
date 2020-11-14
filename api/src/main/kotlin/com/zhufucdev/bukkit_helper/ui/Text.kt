package com.zhufucdev.bukkit_helper.ui

import com.zhufucdev.bukkit_helper.Context

/**
 * Represents a readable visual content.
 */
class Text: () -> String {
    private val impl: () -> String

    /**
     * Constructs a supplier text, which is convenient for user-triggered language change.
     */
    constructor(supplier: () -> String) {
        impl = supplier
    }

    /**
     * Constructs a constant text.
     */
    constructor(constant: String) {
        impl = { constant }
    }

    /**
     * Constructs a resource text.
     */
    constructor(resID: Int) {
        impl = { Context.getString(resID) }
    }

    override fun invoke(): String = impl.invoke()
}