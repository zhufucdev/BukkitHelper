package com.zhufucdev.bukkit_helper

import java.util.*

/**
 * Use [Context.Companion] to fetch internal info.
 */
interface Context {
    fun getString(id: Int, vararg args: String): String

    // Settings
    val locale: Locale
    val dynamicRefreshInterval: DynamicRefreshInterval

    /**
     * Provides internal info which is available in the app, e.g. translations and preferences.
     */
    companion object: Context {
        private var mImplement: Context? = null
        private val impl: Context
            get() = mImplement ?: throw NotImplementedError("API not ready.")

        fun setImplementation(context: Context) {
            val validate = Thread.currentThread().stackTrace
                .any { it.className == "com.zhufucdev.bukkithelper.impl.CommonContext" }
            if (!validate) // Not accept implementation from elsewhere
                throw IllegalArgumentException("context")
            mImplement = context
        }

        /**
         * Android resources that provide translations. Reserved for internal use.
         * @param id The resource ID, e.g. R.string.app_name.
         * @param args Arguments to be used.
         */
        override fun getString(id: Int, vararg args: String): String = impl.getString(id, *args)

        /**
         * App preferred locale.
         */
        override val locale: Locale
            get() = impl.locale

        /**
         * User preferred intervals between each two adjacent actions where a particular plugin fetches data from server.
         */
        override val dynamicRefreshInterval: DynamicRefreshInterval
            get() = impl.dynamicRefreshInterval
    }
}