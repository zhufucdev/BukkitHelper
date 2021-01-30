package com.zhufucdev.bukkithelper.impl.chart

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.chart.ChartElement
import com.zhufucdev.bukkit_helper.chart.ChartType
import com.zhufucdev.bukkithelper.R

/**
 * Utility that binds [Chart] with MPChart
 */
object ChartParser {
    private val binding = hashMapOf<Chart, ChartHolder>()
    fun getBinding(chart: Chart): ChartHolder? = binding[chart]

    private fun bind(chart: Chart, to: com.github.mikephil.charting.charts.Chart<*>) {
        when (to) {
            is LineChart -> {
                val line = LineData()
                fun toEntry(element: ChartElement) = Entry(element.x, element.y).apply { data = element }
                fun invalidate() {
                    line.notifyDataChanged()
                    to.apply {
                        data = line
                        postInvalidate()
                    }
                }
                // <editor-fold desc="Link data">
                chart.series.forEach { s ->
                    val dataArray = arrayListOf<Entry>()
                    s.data.forEach { element ->
                        dataArray.add(toEntry(element))
                    }
                    val dataSet = LineDataSet(dataArray, s.label.invoke())
                    s.data.addAdditionListener { element, _ ->
                        dataSet.addEntry(toEntry(element))
                        invalidate()
                    }
                    s.data.addRemovalListener { _, i ->
                        dataSet.removeEntry(i)
                        invalidate()
                    }
                    line.addDataSet(dataSet)
                }
                // </editor-fold>
                // <editor-fold desc="Set up formatter">
                chart.xFormat?.let { to.xAxis.valueFormatter = ChartFormatter(chart, it) }
                chart.yFormat?.let { to.axisLeft.valueFormatter = ChartFormatter(chart, it) }
                chart.rightYFormat?.let { to.axisRight.valueFormatter = ChartFormatter(chart, it) }
                // </editor-fold>
                // UI
                invalidate()
            }
        }
        to.tag = chart
    }

    private fun bind(chart: Chart, toolbar: Toolbar) {
        toolbar.title = chart.label.invoke()
        MenuInflater(toolbar.context).inflate(R.menu.chart_title_menu, toolbar.menu)
    }

    fun bind(chart: Chart, to: ChartHolder) {
        val colorOnSurface =
            if (to.itemView.context.resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            )
                Color.WHITE
            else
                Color.BLACK

        val viewType: Pair<Class<*>, com.github.mikephil.charting.charts.Chart<*>.() -> Unit> = when (chart.type) {
            ChartType.LINE -> LineChart::class.java to {
                val application: AxisBase.() -> Unit = {
                    textColor = colorOnSurface
                    axisLineColor = colorOnSurface
                    gridColor = colorOnSurface
                }
                (this as LineChart).axisLeft.apply(application)
                axisRight.apply(application)
                xAxis.apply(application)
                legend.textColor = colorOnSurface
            }
        }
        val view =
            (if (to.holder.childCount == 1 && to.holder.children.first()::class.java == viewType)
                to.holder.children.first()
            else {
                to.holder.removeAllViews()
                (viewType.first.getConstructor(Context::class.java).newInstance(to.holder.context))
                    .apply { to.holder.addView(this as View) }
            }) as com.github.mikephil.charting.charts.Chart<*>
        view.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            view.context.resources.getDimensionPixelSize(R.dimen.chart_size_normal)
        )
        view.apply(viewType.second)
        bind(chart, view)
        bind(chart, to.toolbar)
        binding[chart] = to
        to.chart = chart
    }
}