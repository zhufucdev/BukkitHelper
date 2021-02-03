package com.zhufucdev.bukkit_helper.chart.configuration

import com.zhufucdev.bukkit_helper.chart.ChartConfiguration
import com.zhufucdev.bukkit_helper.chart.ChartType
import com.zhufucdev.bukkit_helper.chart.ValueFormat
import com.zhufucdev.bukkit_helper.ui.data.Text

class BarChartConfiguration(
    description: Text = Text.EMPTY,
    xFormat: ValueFormat? = null,
    val leftFormat: ValueFormat? = null,
    val rightFormat: ValueFormat? = null,
    val drawValueAboveBar: Boolean = false,
    val showMaxValue: Boolean = false
) :
    ChartConfiguration(ChartType.BAR, description, xFormat)