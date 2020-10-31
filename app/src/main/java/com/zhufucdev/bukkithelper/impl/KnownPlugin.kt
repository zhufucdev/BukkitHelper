package com.zhufucdev.bukkithelper.impl

import com.zhufucdev.bukkit_helper.Context
import com.zhufucdev.bukkit_helper.Plugin
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.impl.builtin.Descriptor

class KnownPlugin<T: Plugin>(private val clazz: Class<T>) : AbstractPlugin() {
    override val name: String
        get() = clazz.simpleName
    override val description: String =
        Context.getString(
            clazz.getAnnotation(Descriptor::class.java)?.description
                ?: R.string.text_no_description
        )

    override fun mLoad() {
        mInstance = clazz.newInstance()
    }

    override fun mUnload() {
        mInstance?.onDisable()
        mInstance = null
    }
}