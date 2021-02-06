package com.zhufucdev.bukkit_helper.chart.configuration

import com.zhufucdev.bukkit_helper.chart.ChartConfiguration
import com.zhufucdev.bukkit_helper.chart.ChartType
import com.zhufucdev.bukkit_helper.chart.Series
import com.zhufucdev.bukkit_helper.chart.ValueFormat
import com.zhufucdev.bukkit_helper.ui.data.Text

class LineChartConfiguration : ChartConfiguration {
    val leftFormat: ValueFormat?
    val rightFormat: ValueFormat?
    val drawDot: Boolean
    val drawDotHole: Boolean
    val modes: Map<Series, Mode>?
    val commonMode: Mode?

    constructor(
        description: Text = Text.EMPTY,
        xFormat: ValueFormat? = null,
        leftFormat: ValueFormat? = null,
        rightFormat: ValueFormat? = null,
        dot: Boolean = true,
        dotHole: Boolean = true,
        commonMode: Mode = Mode.LINEAR
    ) : super(ChartType.LINE, description, xFormat) {
        this.leftFormat = leftFormat
        this.rightFormat = rightFormat
        this.drawDot = dot
        this.drawDotHole = dotHole

        modes = null
        this.commonMode = commonMode
    }

    constructor(
        description: Text = Text.EMPTY,
        xFormat: ValueFormat? = null,
        leftFormat: ValueFormat? = null,
        rightFormat: ValueFormat? = null,
        dot: Boolean = true,
        dotHole: Boolean = true,
        vararg mode: Pair<Series, Mode>
    ) : super(ChartType.LINE, description, xFormat) {
        this.leftFormat = leftFormat
        this.rightFormat = rightFormat
        this.drawDot = dot
        this.drawDotHole = dotHole

        modes = mapOf(*mode)
        commonMode = null
    }

    enum class Mode {
        CUBIC_BEZIER, STEPPED, LINEAR
    }
}