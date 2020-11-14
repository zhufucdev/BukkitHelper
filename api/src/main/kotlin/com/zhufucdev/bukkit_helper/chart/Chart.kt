package com.zhufucdev.bukkit_helper.chart

import com.zhufucdev.bukkit_helper.DynamicList

/**
 * Represents a set of [Series], which can be drawn on screen.
 */
class Chart {
    val series = DynamicList<Series>()
    val type: ChartType
    var xFormat: ValueFormat? = null
    var yFormat: ValueFormat? = null
    var rightYFormat: ValueFormat? = null

    constructor(series: List<Series>, type: ChartType) : this(type) {
        this.series.addAll(series)
    }

    constructor(series: Series, type: ChartType) : this(type) {
        this.series.add(series)
    }

    constructor(type: ChartType) {
        this.type = type
    }
}