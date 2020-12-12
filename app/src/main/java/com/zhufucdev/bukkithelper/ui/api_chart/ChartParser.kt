package com.zhufucdev.bukkithelper.ui.api_chart

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zhufucdev.bukkit_helper.DynamicList
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.chart.ChartElement
import com.zhufucdev.bukkit_helper.chart.ChartType
import com.zhufucdev.bukkithelper.R

/**
 * Utility that binds [Chart] with MPChart
 */
object ChartParser {
    private val binding = hashMapOf<Chart, com.github.mikephil.charting.charts.Chart<*>>()
    fun getBinding(chart: Chart): com.github.mikephil.charting.charts.Chart<*>? = binding[chart]

    fun bind(chart: Chart, to: com.github.mikephil.charting.charts.Chart<*>) {
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
        binding[chart] = to
    }

    fun bind(chart: Chart, to: ChartViewAdapter.ChartHolder) {
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
            (if (to.root.childCount == 1 && to.root.children.first()::class.java == viewType)
                to.root.children.first()
            else {
                to.root.removeAllViews()
                (viewType.first.getConstructor(Context::class.java).newInstance(to.root.context))
                    .apply { to.root.addView(this as View) }
            }) as com.github.mikephil.charting.charts.Chart<*>
        view.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            view.context.resources.getDimensionPixelSize(R.dimen.chart_size_normal)
        )
        view.apply(viewType.second)
        bind(chart, view)
    }
}