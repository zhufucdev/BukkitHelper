package com.zhufucdev.bukkithelper.impl.link

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.ui.UserInterface
import com.zhufucdev.bukkit_helper.workflow.Link
import com.zhufucdev.bukkit_helper.workflow.Linkable
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.ui.api_chart.ChartParser
import com.zhufucdev.bukkithelper.ui.plugin_ui.UIHolder

/**
 * An implementation of [Link] that collects all its instance.
 */
class CommonLink(from: Linkable, primaryDestination: Linkable, secondaryDestinations: List<Linkable>) :
    Link(from, primaryDestination, secondaryDestinations) {
    private lateinit var navController: NavController

    init {
        fun asComponent(component: Component) {
            component.addImplementedListener {
                val view = UIHolder[component.ui]?.findViewWithTag<View?>(component)
                    ?: error(from, primaryDestination, "ui is not implemented.")

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
                    val view = ChartParser.getBinding(from) ?: error(from, primaryDestination, "chart not implemented.")
                    view.setOnClickListener {
                        performPrimary()
                    }
                    navController = view.findNavController()
                }
            }
            else -> throw UnsupportedOperationException("${from::class.simpleName} as starting point is not supported.")
        }
    }

    private fun navigateTo(dest: Linkable) {
        fun asUI(ui: UserInterface) {
            val bundle = bundleOf("ui" to ui)
            navController.navigate(R.id.action_navigation_home_to_navigation_plugin_ui, bundle)
        }
        when (dest) {
            is UserInterface -> {
                asUI(dest)
            }
            is Component -> {
                asUI(dest.ui ?: error(from, dest, "ui not implemented."))
            }
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
            val view = UIHolder[component.ui]?.findViewWithTag<View?>(from)
                ?: error(from, primaryDestination, "ui is not implemented.")

            view.setOnClickListener(null)
            return view
        }
        when (from) {
            is Component -> asComponent(from)
            is UserInterface -> asComponent(from.rootComponent)
        }
    }

    companion object {
        fun init() {
            setImplementation { CommonLinkBuilder() }
        }

        private fun error(a: Linkable, b: Linkable, msg: String): Nothing =
            kotlin.error("Unable to build link between ${a.label.invoke()} and ${b.label.invoke()}: $msg")
    }
}