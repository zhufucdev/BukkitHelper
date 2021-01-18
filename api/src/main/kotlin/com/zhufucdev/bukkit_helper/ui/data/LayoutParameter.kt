package com.zhufucdev.bukkit_helper.ui.data

import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.GroupComponent

/**
 * A wrap of [HashMap] that redraws the [parent] whenever the [values] changed.
 */
class LayoutParameter<T>(val parent: GroupComponent) : MutableMap<Component, T> {
    private val wrap = hashMapOf<Component, T>()
    override val entries: MutableSet<MutableMap.MutableEntry<Component, T>>
        get() = wrap.entries
    override val keys: MutableSet<Component>
        get() = wrap.keys
    override val size: Int
        get() = wrap.size
    override val values: MutableCollection<T>
        get() = wrap.values

    override fun containsKey(key: Component): Boolean = wrap.containsKey(key)

    override fun containsValue(value: T): Boolean = wrap.containsValue(value)

    override fun get(key: Component): T? = wrap[key]

    override fun isEmpty(): Boolean = wrap.isEmpty()

    override fun clear() {
        wrap.clear()
        parent.redraw()
    }

    override fun put(key: Component, value: T): T? {
        val previous = wrap.put(key, value)
        parent.redraw()
        return previous
    }

    override fun putAll(from: Map<out Component, T>) {
        wrap.putAll(from)
        parent.redraw()
    }

    override fun remove(key: Component): T? {
        val removal = wrap.remove(key)
        parent.redraw()
        return removal
    }
}