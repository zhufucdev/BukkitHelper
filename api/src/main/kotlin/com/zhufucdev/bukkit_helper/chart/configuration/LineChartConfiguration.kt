package com.zhufucdev.bukkit_helper.chart.configuration

import com.zhufucdev.bukkit_helper.chart.ChartConfiguration
import com.zhufucdev.bukkit_helper.chart.ChartType
import com.zhufucdev.bukkit_helper.chart.ValueFormat
import com.zhufucdev.bukkit_helper.ui.data.Text

class LineChartConfiguration(
    description: Text = Text.EMPTY,
    xFormat: ValueFormat? = null,
    val leftFormat: ValueFormat? = null,
    val rightFormat: ValueFormat? = null,
    val dot: Boolean = true,
    val dotHole: Boolean = true,
    val mode: Mode = Mode.LINEAR
) :
    ChartConfiguration(ChartType.LINE, description, xFormat) {
    enum class Mode {
        CUBIC_BEZIER, STEPPED, LINEAR
    }
}