package com.zhufucdev.bukkit_helper

/**
 * A [DynamicList] that removes first few elements when its size may
 * be over [poolSize].
 */
class DynamicPool<T>(val poolSize: Int): DynamicList<T>() {
    override fun add(element: T): Boolean {
        if (poolSize - size == 1) {
            removeFirst()
        }
        return super.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val d = size + elements.size - poolSize
        if (d > 0) {
            for (i in 0 until d) removeFirst()
        }
        return super.addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean = throw UnsupportedOperationException()
}