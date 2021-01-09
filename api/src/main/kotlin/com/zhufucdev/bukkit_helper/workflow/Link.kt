package com.zhufucdev.bukkit_helper.workflow

/**
 * Represents a trigger by clicking and long-clicking
 * between two or more [Linkable]s.
 *
 * The [primaryDestination] is has the priority of being
 * navigated to with a simple click on the [from] widget.
 *
 * Each of [secondaryDestinations] is displayed in a popup list
 * after long-clicking on the [from] widget.
 */
abstract class Link constructor(
    val from: Linkable,
    val primaryDestination: Navigatable,
    val secondaryDestinations: List<Navigatable>
) {
    /**
     * Perform the primary trigger manually.
     */
    abstract fun performPrimary()

    /**
     * Perform the secondary trigger
     * at a point of screen manually.
     */
    abstract fun performSecondary(top: Int, right: Int)

    /**
     * Cancel the link.
     */
    abstract fun disconnect()

    interface LinkBuilder {
        /**
         * Single [Linkable] that can be triggered with.
         * @see Link
         */
        fun from(linkable: Linkable): LinkBuilder

        /**
         * Multiple [Navigatable]s to be navigated to.
         *
         * The first one will be the [primaryDestination], which has the priority of
         * being navigated to with a simple click.
         * @see Link
         */
        fun to(linkable: Navigatable): LinkBuilder

        fun build(): Link
    }

    companion object {
        private var impl: (() -> LinkBuilder)? = null
        fun setImplementation(builder: () -> LinkBuilder) {
            this.impl = builder
        }
        /**
         * A factory utility that constructs [Link].
         */
        fun builder() = impl?.invoke() ?: error("API not ready.")
    }
}