package com.zhufucdev.bukkit_helper.chart

import com.zhufucdev.bukkit_helper.DynamicList
import com.zhufucdev.bukkit_helper.Implementable
import com.zhufucdev.bukkit_helper.ui.Text
import com.zhufucdev.bukkit_helper.workflow.Linkable

/**
 * Represents a set of [Series], which can be drawn on screen.
 */
class Chart : Implementable {
    val series = DynamicList<Series>()
    override val label: Text
    val type: ChartType
    var xFormat: ValueFormat? = null
    var yFormat: ValueFormat? = null
    var rightYFormat: ValueFormat? = null

    constructor(series: List<Series>, type: ChartType, label: Text) : this(type, label) {
        this.series.addAll(series)
    }

    constructor(series: Series, type: ChartType, label: Text) : this(type, label) {
        this.series.add(series)
    }

    constructor(type: ChartType, label: Text) {
        this.type = type
        this.label = label
    }
}