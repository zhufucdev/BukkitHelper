package com.zhufucdev.bukkithelper.impl.link

import com.zhufucdev.bukkit_helper.workflow.Link
import com.zhufucdev.bukkit_helper.workflow.Linkable

class CommonLinkBuilder : Link.LinkBuilder {
    private val destinations = arrayListOf<Linkable>()
    private var from: Linkable? = null

    override fun from(linkable: Linkable): Link.LinkBuilder {
        from = linkable
        return this
    }

    override fun to(linkable: Linkable): Link.LinkBuilder {
        if (!destinations.contains(linkable))
            destinations.add(linkable)
        return this
    }

    override fun build(): Link = CommonLink(checkNotNull(from), destinations.first(), destinations.drop(1))
}