package com.zhufucdev.bukkithelper.impl

import com.zhufucdev.bukkithelper.manager.PluginManager

/**
 * Stores a series of [AbstractPlugin], able to enable and disable each of which.
 */
class PluginPartition(
    private val wrap: List<AbstractPlugin>,
    private val enabledListeners: List<(AbstractPlugin, Throwable?) -> Unit>,
    private val disabledListeners: List<(AbstractPlugin, Throwable?) -> Unit>
) : List<AbstractPlugin> {

    /**
     * Enable any parted plugin. If a plugin is not loaded, it will be loaded first.
     * @return A [Map] mapping each [AbstractPlugin] that failed to be enabled with its [Throwable].
     */
    fun enableAll(): Map<AbstractPlugin, Throwable> {
        val exceptions = hashMapOf<AbstractPlugin, Throwable>()
        this.forEach {
            if (it.status < PluginManager.Status.IN_LOAD)
                PluginManager.load(it)
                    ?.let { e -> exceptions[it] = e; return@forEach } // Give up if load attempt was failed.

            PluginManager.enable(it)?.let { e ->
                exceptions[it] = e
                enabledListeners.forEach { l -> l.invoke(it, e) }
            }
        }
        return exceptions
    }

    /**
     * Disable any enabled plugin of this partition.
     * @return A [Map] mapping each [AbstractPlugin] that failed to be disabled with its [Throwable].
     */
    fun disableAll(): Map<AbstractPlugin, Throwable> {
        val exceptions = hashMapOf<AbstractPlugin, Throwable>()
        this.forEach {
            if (it.status < PluginManager.Status.AFTER_ENABLE)
                return@forEach
            PluginManager.disable(it)?.let { e ->
                exceptions[it] = e
                disabledListeners.forEach { l -> l.invoke(it, e) }
            }
        }
        return exceptions
    }

    override val size: Int
        get() = wrap.size

    override fun contains(element: AbstractPlugin): Boolean = wrap.contains(element)

    override fun containsAll(elements: Collection<AbstractPlugin>): Boolean = wrap.containsAll(elements)

    override fun get(index: Int): AbstractPlugin = wrap[index]

    override fun indexOf(element: AbstractPlugin): Int = wrap.indexOf(element)

    override fun isEmpty(): Boolean = wrap.isEmpty()

    override fun iterator(): Iterator<AbstractPlugin> = wrap.iterator()

    override fun lastIndexOf(element: AbstractPlugin): Int = wrap.lastIndexOf(element)

    override fun listIterator(): ListIterator<AbstractPlugin> = wrap.listIterator()

    override fun listIterator(index: Int): ListIterator<AbstractPlugin> = wrap.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): PluginPartition =
        PluginPartition(wrap.subList(fromIndex, toIndex), enabledListeners, disabledListeners)
}