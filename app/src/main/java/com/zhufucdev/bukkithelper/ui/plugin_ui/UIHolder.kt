package com.zhufucdev.bukkithelper.ui.plugin_ui

import android.view.View
import com.zhufucdev.bukkit_helper.ui.UserInterface

/**
 * A wrap of [HashMap] that holds [UserInterface] with its root view.
 * The UIHolder may be modified by the following activities:
 * - [PluginUIFragment.onCreateView]
 */
object UIHolder: MutableMap<UserInterface, View> {
    private val wrap = hashMapOf<UserInterface, View>()
    override val size: Int
        get() = wrap.size

    override fun containsKey(key: UserInterface): Boolean = wrap.containsKey(key)

    override fun containsValue(value: View): Boolean = wrap.containsValue(value)

    override fun get(key: UserInterface): View? = wrap[key]

    override fun isEmpty(): Boolean = wrap.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<UserInterface, View>>
        get() = wrap.entries
    override val keys: MutableSet<UserInterface>
        get() = wrap.keys
    override val values: MutableCollection<View>
        get() = wrap.values

    override fun clear() {
        wrap.clear()
    }

    override fun put(key: UserInterface, value: View): View? = wrap.put(key, value)

    override fun putAll(from: Map<out UserInterface, View>) = wrap.putAll(from)

    override fun remove(key: UserInterface): View? = wrap.remove(key)
}