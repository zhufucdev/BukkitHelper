package com.zhufucdev.bukkithelper.impl.link

import com.zhufucdev.bukkit_helper.workflow.Link
import com.zhufucdev.bukkit_helper.workflow.Linkable
import com.zhufucdev.bukkit_helper.workflow.Navigatable

class CommonLinkBuilder : Link.LinkBuilder {
    private val destinations = arrayListOf<Navigatable>()
    private var from: Linkable? = null

    override fun from(linkable: Linkable): Link.LinkBuilder {
        from = linkable
        return this
    }

    override fun to(linkable: Navigatable): Link.LinkBuilder {
        if (!destinations.contains(linkable))
            destinations.add(linkable)
        return this
    }

    override fun build(): Link = CommonLink(checkNotNull(from), destinations.first(), destinations.drop(1))
}