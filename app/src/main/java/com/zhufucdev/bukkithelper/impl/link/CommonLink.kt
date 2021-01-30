package com.zhufucdev.bukkithelper.impl.link

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
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
                    val holder = ChartParser.getBinding(from) ?: error(from, primaryDestination, "chart not implemented.")
                    holder.parent.setOpenListener {
                        if (it == from) performPrimary()
                    }
                    navController = holder.toolbar.findNavController()
                }
            }
            else -> throw UnsupportedOperationException("${from::class.simpleName} as starting point is not supported.")
        }

        Linker.checkAndListen(this)
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
        when (from) {
            is Component -> asComponent(from)
            is UserInterface -> asComponent(from.rootComponent)
            else -> throw UnsupportedOperationException("Disconnecting ${from::class.simpleName} is not supported.")
        }
    }

    companion object {
        fun init() {
            setImplementation { CommonLinkBuilder() }
        }

        fun error(a: Linkable, b: Navigatable, msg: String): Nothing {
            val labelB = if (b is Linkable) b.label.invoke() else b::class.simpleName
            kotlin.error("Unable to build link between ${a.label.invoke()} and $labelB: $msg")
        }
    }
}