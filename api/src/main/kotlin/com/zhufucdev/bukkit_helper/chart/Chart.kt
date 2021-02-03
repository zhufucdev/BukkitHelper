package com.zhufucdev.bukkit_helper.chart

import com.zhufucdev.bukkit_helper.DynamicList
import com.zhufucdev.bukkit_helper.Implementable
import com.zhufucdev.bukkit_helper.ui.data.Text

/**
 * Represents a set of [Series], which can be drawn on screen.
 */
class Chart : Implementable {
    val series = DynamicList<Series>()
    override val label: Text
    val configuration: ChartConfiguration

    val type: ChartType get() = configuration.type

    constructor(series: List<Series>, label: Text, configuration: ChartConfiguration)
            : this(label, configuration) {
        this.series.addAll(series)
    }

    constructor(series: Series, label: Text, configuration: ChartConfiguration)
            : this(label, configuration) {
        this.series.add(series)
    }

    constructor(label: Text, configuration: ChartConfiguration) {
        this.label = label
        this.configuration = configuration
    }
}