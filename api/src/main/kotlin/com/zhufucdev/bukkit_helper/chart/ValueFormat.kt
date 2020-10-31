package com.zhufucdev.bukkit_helper.chart

abstract class ValueFormat {
    open fun label(element: ChartElement): String = element.x.toString()
    open fun axis(value: Float): String = value.toString()
}