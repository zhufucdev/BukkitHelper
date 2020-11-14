package com.zhufucdev.bukkit_helper

/**
 * A wrapper of [ArrayList] that monitors changes of its elements.
 */
class DynamicList<T> : MutableList<T> {
    constructor()

    constructor(wrap: List<T>) {
        this.wrap.addAll(wrap)
    }

    private val wrap = arrayListOf<T>()
    private val changeListeners = arrayListOf<(List<T>) -> Unit>()
    private val additionListeners = arrayListOf<(T, Int) -> Unit>()
    private val removalListeners = arrayListOf<(T, Int) -> Unit>()
    private val elementChangeListeners = arrayListOf<(T, T, Int) -> Unit>()

    /**
     * Make a listener of element addition and removal work.
     * @param l the listener
     */
    fun addChangeListener(l: (List<T>) -> Unit) {
        if (!changeListeners.contains(l))
            changeListeners.add(l)
    }

    /**
     * Make a listener of element addition work.
     * @param l the listener
     */
    fun addAdditionListener(l: (T, Int) -> Unit) {
        if (!additionListeners.contains(l))
            additionListeners.add(l)
    }

    /**
     * Make a listener of element removal work.
     * @param l the listener
     */
    fun addRemovalListener(l: (T, Int) -> Unit) {
        if (!removalListeners.contains(l))
            removalListeners.add(l)
    }

    /**
     * Make a listener of element replacement work.
     * @param l The listener with new element, old element and position where the replacement happened.
     */
    fun addElementChangeListener(l: (T, T, Int) -> Unit) {
        if (!elementChangeListeners.contains(l))
            elementChangeListeners.add(l)
    }

    /**
     * Make a listener of element addition or removal not work.
     * @param l the listener
     * @return true if the listener used to be working and the removal is successful, false otherwise.
     */
    fun removeChangeListener(l: () -> Unit): Boolean = changeListeners.remove(l)

    /**
     * Make a listener of element addition not work.
     * @param l the listener
     * @return true if the listener used to be working and the removal is successful, false otherwise.
     */
    fun removeAdditionListener(l: (T, Int) -> Unit) = additionListeners.remove(l)

    /**
     * Make a listener of element removal not work.
     * @param l the listener
     * @return true if the listener used to be working and the removal is successful, false otherwise.
     */
    fun removeRemovalListener(l: (T) -> Unit) = removalListeners.remove(l)

    /**
     * Make a listener of element replacement not work.
     * @param l The listener with new element, old element and position where the replacement happened.
     * @return true if the listener used to be working and the removal is successful, false otherwise.
     */
    fun removeElementChangeListener(l: (T, T, Int) -> Unit) = elementChangeListeners.remove(l)

    private fun notifyChanges() {
        changeListeners.forEach { it.invoke(wrap.clone() as List<T>) }
    }

    private fun notifyAddition(element: T, index: Int) {
        additionListeners.forEach { it.invoke(element, index) }
    }

    private fun notifyRemoval(element: T, index: Int) {
        removalListeners.forEach { it.invoke(element, index) }
    }

    private fun notifyReplacement(element: T, old: T, index: Int) {
        elementChangeListeners.forEach { it.invoke(element, old, index) }
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

    override fun subList(fromIndex: Int, toIndex: Int): DynamicList<T> = DynamicList(wrap.subList(fromIndex, toIndex))

    override fun add(element: T): Boolean = wrap.add(element).also {
        if (it) {
            notifyChanges()
            notifyAddition(element, lastIndex)
        }
    }

    override fun add(index: Int, element: T) =
        wrap.add(index, element).also { notifyChanges(); notifyAddition(element, index) }

    override fun addAll(index: Int, elements: Collection<T>): Boolean = wrap.addAll(index, elements)
        .also {
            if (it) {
                notifyChanges()
                elements.forEachIndexed { i, e ->
                    notifyAddition(e, i + index)
                }
            }
        }

    override fun addAll(elements: Collection<T>): Boolean = addAll(elements)
        .also {
            if (it) {
                notifyChanges()
                elements.forEachIndexed { i, e ->
                    notifyAddition(e, size - elements.size + i)
                }
            }
        }

    override fun clear() {
        if (removalListeners.isNotEmpty()) {
            val clone = this.wrap.clone() as List<T>
            wrap.clear()
            notifyChanges()
            clone.forEachIndexed { i, e -> notifyRemoval(e, i) }
        } else {
            wrap.clear()
            notifyChanges()
        }
    }

    override fun remove(element: T): Boolean {
        return if (changeListeners.isNotEmpty()) {
            val index = this.indexOf(element)
            wrap.remove(element)
                .also {
                    if (it) {
                        notifyChanges()
                        notifyAddition(element, index)
                    }
                }
        } else {
            wrap.remove(element).also { notifyChanges() }
        }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return if (removalListeners.isNotEmpty()) {
            val clone = wrap.clone() as List<T>
            wrap.removeAll(elements)
                .also {
                    if (it) {
                        notifyChanges()
                        elements.forEach { e -> notifyRemoval(e, clone.indexOf(e)) }
                    }
                }
        } else {
            wrap.retainAll(elements).also { notifyChanges() }
        }
    }

    override fun removeAt(index: Int): T = wrap.removeAt(index).also { notifyChanges(); notifyRemoval(it, index) }

    override fun retainAll(elements: Collection<T>): Boolean {
        return if (removalListeners.isNotEmpty()) {
            val clone = wrap.clone() as List<T>
            wrap.retainAll(elements)
                .also { _ ->
                    notifyChanges()
                    clone.forEachIndexed { i, e -> if (!elements.contains(e)) notifyRemoval(e, i) }
                }
        } else {
            wrap.retainAll(elements).also { notifyChanges() }
        }
    }

    override fun set(index: Int, element: T): T = wrap.set(index, element)
        .also {
            notifyChanges()
            if (it != element) notifyReplacement(element, it, index)
        }
}