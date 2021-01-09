package com.zhufucdev.bukkit_helper.workflow

/**
 * Represents a destination to be executed when navigated to.
 */
abstract class Executable<T>(private val invoke: (T) -> Unit) : Navigatable, (T) -> Unit {
    override fun invoke(p1: T) {
        invoke.invoke(p1)
    }
}