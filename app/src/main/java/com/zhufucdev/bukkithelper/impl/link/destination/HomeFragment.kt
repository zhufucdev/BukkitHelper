package com.zhufucdev.bukkithelper.impl.link.destination

import com.zhufucdev.bukkit_helper.ui.data.Text
import com.zhufucdev.bukkit_helper.workflow.Linkable
import com.zhufucdev.bukkithelper.R

object HomeFragment : Linkable {
    override val label: Text
        get() = Text(R.string.app_name)
}