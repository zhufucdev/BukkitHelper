package com.zhufucdev.bukkithelper.ui

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.chart.ChartElement
import com.zhufucdev.bukkit_helper.chart.ValueFormat

/**
 * A [ValueFormatter] works with [ChartParser].
 */
class ChartFormatter : ValueFormatter {
    val formatter: ValueFormat?
    val chart: Chart

    constructor(chart: Chart, format: (ChartElement) -> String) : this(
        chart,
        object : ValueFormat() {
            override fun label(element: ChartElement): String = format.invoke(element)
        }
    )

    constructor(chart: Chart, format: ValueFormat) {
        this.formatter = format
        this.chart = chart
    }

    /**
     * Priority of formatting:
     * 1. [ValueFormat]
     * 2. [ChartElement.label]
     * 3. [ValueFormatter.getAxisLabel]
     */
    override fun getAxisLabel(value: Float, axis: AxisBase?): String = formatter?.axis(value)
        ?: chart.series.first().data.firstOrNull { it.x == value }?.takeIf { it.hasLabel }?.label
        ?: super.getAxisLabel(value, axis)

    override fun getPointLabel(entry: Entry): String = (entry.data as ChartElement).takeIf { it.hasLabel }?.label
        ?: super.getPointLabel(entry)
}