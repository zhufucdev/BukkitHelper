package com.zhufucdev.bukkithelper.ui.plugin_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zhufucdev.bukkit_helper.ui.GroupComponent
import com.zhufucdev.bukkit_helper.ui.UserInterface
import com.zhufucdev.bukkithelper.ui.api_ui.UIParser

/**
 * A [Fragment] that works with [UIParser].
 */
class PluginUIFragment : Fragment() {

    private lateinit var ui: UserInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = requireArguments().get("ui") as UserInterface

        ui.setRedrawImplementation {
            val view = requireView().findViewWithTag<View>(it) ?: return@setRedrawImplementation false
            if (it is GroupComponent) {
                (view.parent as ViewGroup).apply {
                    removeView(view)
                    addView(UIParser.parse(it, requireContext()))
                }
            } else {
                view.postInvalidate()
            }
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val parse = UIParser.parse(ui.rootComponent, requireContext())
        UIHolder[ui] = parse
        return parse
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ui.markImplemented()
    }
}