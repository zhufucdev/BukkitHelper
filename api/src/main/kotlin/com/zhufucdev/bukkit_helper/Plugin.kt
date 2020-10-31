package com.zhufucdev.bukkit_helper

import com.zhufucdev.bukkit_helper.plugin.*

/**
 * Basic class of plugins, whose instance provides every logic to implement
 * functions of the plugin.
 * Basically a [Plugin] only serves its name on the _Installed Plugins_ list.
 * To reserve content, implement at least one of the following interfaces:
 * - [ChartPlugin]
 * - [UIPlugin]
 */
abstract class Plugin {
    /**
     * Called after the plugin is loaded and server is connected.
     */
    open fun onEnable() {
    }

    /**
     * Called after the plugin was loaded and before the server is disconnected.
     */
    open fun onDisable() {
    }
}