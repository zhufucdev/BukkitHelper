package com.zhufucdev.bukkit_helper.workflow

/**
 * Represents a program-specified [Executable].
 */
class Execute(invoke: () -> Unit) : Executable<Any?>({ invoke.invoke() })