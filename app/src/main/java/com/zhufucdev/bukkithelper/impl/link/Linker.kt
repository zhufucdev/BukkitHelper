package com.zhufucdev.bukkithelper.impl.link

import com.zhufucdev.bukkit_helper.ui.Component
import com.zhufucdev.bukkit_helper.workflow.Link

/**
 * A utility that applies and cancels a [Link].
 */
object Linker {
    fun apply(link: Link) {
        when (val from = link.from) {
            is Component -> {

            }
        }
    }
}