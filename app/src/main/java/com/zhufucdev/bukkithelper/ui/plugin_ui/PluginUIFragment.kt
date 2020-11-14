package com.zhufucdev.bukkithelper.ui.plugin_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhufucdev.bukkit_helper.plugin.UIPlugin
import com.zhufucdev.bukkit_helper.ui.GroupComponent
import com.zhufucdev.bukkit_helper.ui.UserInterface
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.ui.api_ui.UIParser

/**
 * A [Fragment] that works with [UIParser].
 */
class PluginUIFragment(private val ui: UserInterface) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setRedrawImplementation {
            val view = requireView().findViewWithTag<View>(it) ?: return@setRedrawImplementation
            if (it is GroupComponent) {
                (view.parent as ViewGroup).apply {
                    removeView(view)
                    addView(UIParser.parse(it, requireContext()))
                }
            } else {
                view.postInvalidate()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return UIParser.parse(ui.rootComponent, requireContext())
    }
}