package com.zhufucdev.bukkithelper.impl.link

import android.view.View
import androidx.collection.arrayMapOf
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.common.collect.ArrayListMultimap
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.UserInterface
import com.zhufucdev.bukkit_helper.workflow.*
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.impl.UIParser
import com.zhufucdev.bukkithelper.impl.link.destination.HomeFragment
import com.zhufucdev.bukkithelper.impl.chart.ChartParser

/**
 * An implementation of [Link] that collects all its instance.
 */
class CommonLink(from: Linkable, primaryDestination: Navigatable, secondaryDestinations: List<Navigatable>) :
    Link(from, primaryDestination, secondaryDestinations) {
    private lateinit var navController: NavController

    init {
        fun asComponent(component: Component) {
            component.addImplementedListener {
                val view = UIParser.findViewByComponent<View>(component)
                view.setOnClickListener {
                    performPrimary()
                }

                navController = view.findNavController()
            }
        }

        when (from) {
            is Component -> asComponent(from)
            is UserInterface -> asComponent(from.rootComponent)
            is Chart -> {
                from.addImplementedListener {
                    val holder =
                        ChartParser.getBinding(from) ?: error(from, primaryDestination, "chart not implemented.")
                    holder.setOpenListener {
                        performPrimary()
                    }
                    navController = holder.toolbar.findNavController()
                }
            }
            else -> throw UnsupportedOperationException("${from::class.simpleName} as starting point is not supported.")
        }

        Linker.checkAndListen(this)

        markLink(this)
    }

    private fun navigateTo(dest: Navigatable) {
        fun asUI(ui: UserInterface) {
            val bundle = bundleOf("uiCode" to ui.hashCode())
            navController.createDeepLink()
                .setDestination(R.id.navigation_plugin_ui)
                .setArguments(bundle)
                .setGraph(navController.graph)
                .createPendingIntent()
                .send()
        }

        fun asFragment(id: Int) {
            navController.createDeepLink()
                .setDestination(id)
                .setGraph(navController.graph)
                .createPendingIntent()
                .send()
        }

        when (dest) {
            is UserInterface -> asUI(dest)
            is Component -> asUI(dest.ui ?: error(from, dest, "ui not implemented."))
            is HomeFragment -> asFragment(R.id.navigation_home)
            is Execute -> Linker.asExecute(dest)
            else -> error(from, dest, "${dest::class.simpleName} is not to be navigated to manually.")
        }
    }

    override fun performPrimary() {
        navigateTo(primaryDestination)
    }

    override fun performSecondary(top: Int, right: Int) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        fun asComponent(component: Component): View {
            val view = UIParser.findViewByComponent<View>(component)
            view.setOnClickListener(null)
            return view
        }

        fun asChart(chart: Chart) {
            chart.addImplementedListener {
                val holder = ChartParser.getBinding(chart) ?: error("$chart not implemented.")
                holder.setOpenListener(null)
            }
        }
        when (from) {
            is Component -> asComponent(from)
            is UserInterface -> asComponent(from.rootComponent)
            is Chart -> asChart(from)
            else -> throw UnsupportedOperationException("Disconnecting ${from::class.simpleName} is not supported.")
        }
        markDisconnect(this)
    }

    companion object {
        fun init() {
            setImplementation { CommonLinkBuilder() }
        }

        private val links = ArrayListMultimap.create<Linkable, Link>()
        private fun markLink(link: Link) {
            val array = links[link.from]
            if (array.isEmpty())
                referredToListeners[link.from]?.forEach { it(link) }
            if (!array.contains(link))
                array.add(link)
        }

        private fun markDisconnect(link: Link) {
            val array = links[link.from]
            if (array.remove(link) && array.isEmpty())
                notReferredToListeners[link.from]?.forEach { it.invoke() }
        }

        private val referredToListeners = ArrayListMultimap.create<Linkable, (Link) -> Unit>()
        private val notReferredToListeners = ArrayListMultimap.create<Linkable, () -> Unit>()

        /**
         * @param l Called the first time when the [Link].from
         * is linked.
         */
        fun addReferredToListener(to: Linkable, l: (Link) -> Unit) {
            val array = referredToListeners[to]
            if (array.contains(l)) return
            array.add(l)

            if (links.containsKey(to)) l(links[to]!!.first())
        }

        /**
         * @param l Called when all Links have been disconnected
         * from [Link].from.
         */
        fun addNotReferredToListener(to: Linkable, l: () -> Unit) {
            val array = notReferredToListeners[to]
            if (array.contains(l)) return
            array.add(l)

            if (!links.containsKey(to)) l()
        }

        fun error(a: Linkable, b: Navigatable, msg: String): Nothing {
            val labelB = if (b is Linkable) b.label.invoke() else b::class.simpleName
            error("Unable to build link between ${a.label.invoke()} and $labelB: $msg")
        }
    }
}