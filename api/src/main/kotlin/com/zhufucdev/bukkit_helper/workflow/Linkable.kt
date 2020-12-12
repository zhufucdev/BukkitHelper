package com.zhufucdev.bukkit_helper.workflow

import com.zhufucdev.bukkit_helper.Implementable
import com.zhufucdev.bukkit_helper.ui.Text
import java.io.Serializable

/**
 * Represents a widget that can be interacted with
 * to navigate to another widget.
 */
interface Linkable: Serializable {
    val label: Text
}