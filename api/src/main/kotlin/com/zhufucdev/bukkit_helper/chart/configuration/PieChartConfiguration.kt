package com.zhufucdev.bukkit_helper.chart.configuration

import com.zhufucdev.bukkit_helper.chart.ChartConfiguration
import com.zhufucdev.bukkit_helper.chart.ChartType
import com.zhufucdev.bukkit_helper.ui.data.Text

class PieChartConfiguration(
    description: Text = Text.EMPTY,
    val showPercentage: Boolean = true,
    val centerText: Text = Text.EMPTY,
    val drawRadius: Float = 360F
) : ChartConfiguration(ChartType.PIE, description, null)