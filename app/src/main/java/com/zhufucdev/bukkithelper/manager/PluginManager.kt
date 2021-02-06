package com.zhufucdev.bukkithelper.manager

import com.zhufucdev.bukkit_helper.Plugin
import com.zhufucdev.bukkithelper.impl.AbstractPlugin
import com.zhufucdev.bukkithelper.impl.KnownPlugin
import com.zhufucdev.bukkithelper.impl.PluginPartition
import com.zhufucdev.bukkithelper.impl.builtin.PlayerMonitor
import com.zhufucdev.bukkithelper.impl.builtin.TPSMonitor
import kotlin.jvm.internal.Reflection
import kotlin.reflect.full.isSuperclassOf

@Suppress("UNCHECKED_CAST")
object PluginManager {
    var status: Status = Status.BEFORE_LOAD
        private set
    private val mList = arrayListOf<AbstractPlugin>()
    val plugins get() = mList.toList()

    /**
     * Add an [AbstractPlugin] to the plugin pool and load it if [PluginManager] is marked as [Status.AFTER_LOAD].
     * If the [plugin] is already registered, nothing would happen.
     * @return Any [Throwable] if occurred.
     */
    fun register(plugin: AbstractPlugin): Throwable? {
        if (mList.contains(plugin)) return null
        mList.add(plugin)
        if (status == Status.AFTER_LOAD) {
            return load(plugin)
        }
        return null
    }

    internal fun load(plugin: AbstractPlugin) = try {
        plugin.load()
        null
    } catch (t: Throwable) {
        t
    }

    internal fun enable(plugin: AbstractPlugin) = try {
        plugin.enable()
        null
    } catch (t: Throwable) {
        t
    }

    internal fun disable(plugin: AbstractPlugin) = try {
        plugin.disable()
        null
    } catch (t: Throwable) {
        t
    }

    /**
     * Load any registered plugin that is not loaded.
     * @return A [Map] mapping a [AbstractPlugin] that failed to be loaded with its [Throwable].
     */
    fun loadAll(): Map<AbstractPlugin, Throwable> {
        status = Status.IN_LOAD

        val exceptions = hashMapOf<AbstractPlugin, Throwable>()
        mList.forEach {
            if (it.status >= Status.IN_LOAD)
                return@forEach
            load(it)?.let { e -> exceptions[it] = e }
        }

        status = Status.AFTER_LOAD
        return exceptions
    }

    /**
     * Enable any registered plugin. If a plugin is not loaded, it will be loaded first.
     * @return A [Map] mapping a [AbstractPlugin] that failed to be enabled with its [Throwable].
     */
    fun enableAll(): Map<AbstractPlugin, Throwable> {
        status = Status.IN_ENABLE
        val exceptions = PluginPartition(mList, enabledListeners, disabledListeners).enableAll()
        status = Status.AFTER_ENABLE
        return exceptions
    }

    /**
     * Disable any enabled plugin.
     * @return A [Map] mapping a [AbstractPlugin] that failed to be disabled with its [Throwable].
     */
    fun disableAll(): Map<AbstractPlugin, Throwable> {
        status = Status.IN_ENABLE
        val exceptions = PluginPartition(mList, enabledListeners, disabledListeners).disableAll()
        status = Status.AFTER_LOAD
        return exceptions
    }

    /**
     * Get a registered [AbstractPlugin] by name.
     */
    operator fun get(name: String): AbstractPlugin = mList.firstOrNull { it.name == name }
        ?: error("$name is not registered.")

    /**
     * Get registered [AbstractPlugin]s by type.
     */
    operator fun <T> get(clazz: Class<T>) =
        PluginPartition(mList.filter {
            it.status >= Status.AFTER_LOAD && Reflection.createKotlinClass(clazz).isSuperclassOf(it.instance::class)
        }, enabledListeners, disabledListeners)

    private val enabledListeners = arrayListOf<(AbstractPlugin, Throwable?) -> Unit>()
    fun addEnabledListener(l: (AbstractPlugin, Throwable?) -> Unit) {
        if (enabledListeners.contains(l)) return
        enabledListeners.add(l)
    }

    fun removeEnabledListener(l: (AbstractPlugin, Throwable?) -> Unit) {
        enabledListeners.remove(l)
    }

    private val disabledListeners = arrayListOf<(AbstractPlugin, Throwable?) -> Unit>()
    fun addDisabledListener(l: (AbstractPlugin, Throwable?) -> Unit) {
        if (disabledListeners.contains(l)) return
        disabledListeners.add(l)
    }

    fun removeDisabledListener(l: (AbstractPlugin, Throwable?) -> Unit) {
        disabledListeners.remove(l)
    }

    enum class Status {
        BEFORE_LOAD, IN_LOAD, AFTER_LOAD, IN_ENABLE, AFTER_ENABLE
    }

    object BuiltIn {
        val known get() = listOf(TPSMonitor::class.java, PlayerMonitor::class.java)
        fun registerAll() {
            known.forEach { register(KnownPlugin(it as Class<Plugin>)) }
        }
    }
}