package com.zhufucdev.bukkit_helper

/**
 * A wrapper of [ArrayList] that monitors changes of its elements.
 */
class DynamicList<T> : MutableList<T> {
    private val wrap = arrayListOf<T>()
    private val changeListeners = arrayListOf<() -> Unit>()

    /**
     * Make a listener of element addition and removal work.
     * @param l the listener
     */
    fun addChangeListener(l: () -> Unit) {
        if (!changeListeners.contains(l))
            changeListeners.add(l)
    }

    /**
     * Make a listener of element addition or removal not work.
     * @param l the listener
     * @return true if the listener used to be working and the removal is successful, false otherwise.
     */
    fun removeChangeListener(l: () -> Unit): Boolean = changeListeners.remove(l)

    private fun invokeChanges() {
        changeListeners.forEach { it.invoke() }
    }

    override val size: Int
        get() = wrap.size

    override fun contains(element: T): Boolean = wrap.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = wrap.containsAll(elements)

    override fun get(index: Int): T = wrap[index]

    override fun indexOf(element: T): Int = wrap.indexOf(element)

    override fun isEmpty(): Boolean = wrap.isEmpty()

    override fun iterator(): MutableIterator<T> = wrap.iterator()

    override fun lastIndexOf(element: T): Int = wrap.lastIndexOf(element)

    override fun listIterator(): MutableListIterator<T> = wrap.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> = wrap.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = wrap.subList(fromIndex, toIndex)

    override fun add(element: T): Boolean = wrap.add(element).also { if (it) invokeChanges() }

    override fun add(index: Int, element: T) = wrap.add(index, element).also { invokeChanges() }

    override fun addAll(index: Int, elements: Collection<T>): Boolean = wrap.addAll(index, elements)
        .also { if (it) invokeChanges() }

    override fun addAll(elements: Collection<T>): Boolean = addAll(elements).also { if (it) invokeChanges() }

    override fun clear() {
        wrap.clear()
        invokeChanges()
    }

    override fun remove(element: T): Boolean = wrap.remove(element).also { if (it) invokeChanges() }

    override fun removeAll(elements: Collection<T>): Boolean = wrap.removeAll(elements)
        .also { if (it) invokeChanges() }

    override fun removeAt(index: Int): T = wrap.removeAt(index).also { invokeChanges() }

    override fun retainAll(elements: Collection<T>): Boolean = wrap.retainAll(elements).also { invokeChanges() }

    override fun set(index: Int, element: T): T = wrap.set(index, element).also { invokeChanges() }
}