package com.zhufucdev.bukkit_helper.chart

import com.zhufucdev.bukkit_helper.ui.data.Text

abstract class ChartConfiguration(
    val type: ChartType,
    val description: Text = Text.EMPTY,
    val xFormat: ValueFormat? = null
)
