package com.zhufucdev.bukkithelper.impl

import com.zhufucdev.bukkit_helper.Plugin
import com.zhufucdev.bukkithelper.manager.PluginManager

/**
 * Presents a plugin and its information.
 */
abstract class AbstractPlugin {
    /**
     * Unique identity of the plugin.
     */
    abstract val name: String

    /**
     * Short text indicating functions of this plugin.
     */
    abstract val description: String

    /**
     * Field of the plugin instance, providing its functions.
     */
    val instance: Plugin
        get() = mInstance ?: error("Not initialized.")

    /**
     * Make the [instance] initialized.
     */
    fun load() {
        status = PluginManager.Status.IN_LOAD
        try {
            mLoad()
        } catch (e: Throwable) {
            throw e
        } finally {
            status = PluginManager.Status.AFTER_LOAD
        }
    }

    /**
     * Make the [instance] enabled.
     */
    fun enable() {
        if (status < PluginManager.Status.AFTER_LOAD)
            load()
        status = PluginManager.Status.IN_ENABLE
        try {
            mEnable()
        } catch (e: Throwable) {
            throw e
        } finally {
            status = PluginManager.Status.AFTER_ENABLE
        }
    }

    /**
     * Make the [instance] disabled.
     */
    fun disable() {
        if (status < PluginManager.Status.AFTER_ENABLE) // Give up if not enabled
            return

        try {
            mDisable()
        } catch (e: Throwable) {
            throw e
        } finally {
            status = PluginManager.Status.AFTER_LOAD
        }
    }

    /**
     * Unload the [instance].
     */
    fun unload() {
        try {
            mUnload()
        } catch (e: Throwable) {
            throw e
        } finally {
            status = PluginManager.Status.BEFORE_LOAD
        }
    }

    /**
     * Status of this [instance].
     */
    var status: PluginManager.Status = PluginManager.Status.BEFORE_LOAD
        protected set

    /**
     * Implementation of [instance].
     */
    protected var mInstance: Plugin? = null

    /**
     * Implementation of [load].
     */
    protected abstract fun mLoad()

    /**
     * Implementation of [enable].
     */
    protected open fun mEnable() {
        instance.onEnable()
    }

    /**
     * Implementation of [disable].
     */
    protected open fun mDisable() {
        instance.onDisable()
    }

    /**
     * Implementation of [unload].
     */
    protected abstract fun mUnload()
}