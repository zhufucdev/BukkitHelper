package com.zhufucdev.bukkithelper.ui

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.chart.ValueFormat

/**
 * Utility that binds [Chart] with MPChart
 */
object ChartParser {
    fun bind(chart: Chart, to: com.github.mikephil.charting.charts.Chart<*>) {
        when (to) {
            is LineChart -> {
                val line = LineData()
                // <editor-fold desc="Link data">
                if (chart.series.size > 1)
                    chart.series.forEach { s ->
                        val dataSet = arrayListOf<Entry>()
                        s.data.forEach { element ->
                            val entry = Entry(element.x, element.y)
                            entry.data = element
                            dataSet.add(entry)
                        }
                        line.addDataSet(LineDataSet(dataSet, s.label))
                    }
                else
                    chart.series.first().data.forEachIndexed { index, chartElement ->
                        val entry = Entry(chartElement.x, chartElement.y)
                        entry.data = chartElement
                        line.addEntry(entry, index)
                    }
                // </editor-fold>
                // <editor-fold desc="Set up formatter">
                chart.xFormat?.let { to.xAxis.valueFormatter = ChartFormatter(chart, it) }
                chart.yFormat?.let { to.axisLeft.valueFormatter = ChartFormatter(chart, it) }
                chart.rightYFormat?.let { to.axisRight.valueFormatter = ChartFormatter(chart, it) }
                // </editor-fold>
                // UI
                to.apply {
                    data = line
                    invalidate()
                }
            }
        }
    }
}