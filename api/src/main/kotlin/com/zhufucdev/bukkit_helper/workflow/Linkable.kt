package com.zhufucdev.bukkit_helper.workflow

import com.zhufucdev.bukkit_helper.ui.data.Text

/**
 * Represents a widget that can be interacted with
 * to navigate to another widget.
 */
interface Linkable : Navigatable {
    val label: Text
}