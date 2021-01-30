package com.zhufucdev.bukkithelper.impl.chart

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkithelper.R

class ChartHolder(root: View, val parent: ChartViewAdapter) : RecyclerView.ViewHolder(root) {
    val holder: FrameLayout = root.findViewById(R.id.chart_holder)
    val toolbar: Toolbar = root.findViewById(R.id.chart_title)
    lateinit var chart: Chart
}