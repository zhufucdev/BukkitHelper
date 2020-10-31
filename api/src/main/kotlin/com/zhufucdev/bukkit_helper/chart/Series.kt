package com.zhufucdev.bukkit_helper.chart

import com.zhufucdev.bukkit_helper.DynamicList

/**
 * Represents a series of chart consisting of sets of [ChartElement] and a label [String].
 */
class Series(val data: DynamicList<ChartElement>, val label: String)