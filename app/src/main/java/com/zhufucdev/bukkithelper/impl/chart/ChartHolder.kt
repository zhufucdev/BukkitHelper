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

    private var settingsListener: ((Chart) -> Unit)? = null
    private var openListener: ((Chart) -> Unit)? = null

    fun setSettingsListener(l: ((Chart) -> Unit)?) {
        settingsListener = l
    }

    fun setOpenListener(l: ((Chart) -> Unit)?) {
        openListener = l
    }

    init {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.chart_settings -> settingsListener?.invoke(chart) != null
                R.id.chart_open -> openListener?.invoke(chart) != null
                else -> false
            }
        }
    }
}