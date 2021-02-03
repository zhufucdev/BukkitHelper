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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.*
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.chart.ChartElement
import com.zhufucdev.bukkit_helper.chart.ChartType
import com.zhufucdev.bukkit_helper.chart.configuration.BarChartConfiguration
import com.zhufucdev.bukkit_helper.chart.configuration.LineChartConfiguration
import com.zhufucdev.bukkit_helper.chart.configuration.PieChartConfiguration
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.android
import com.zhufucdev.bukkithelper.plus
import kotlin.reflect.KCallable

/**
 * Utility that binds [Chart] with MPChart.
 */
object ChartParser {
    private val binding = hashMapOf<Chart, ChartHolder>()
    fun getBinding(chart: Chart): ChartHolder? = binding[chart]

    /**
     * Make [chart]'s **data** sync with [to].
     */
    private fun bind(chart: Chart, to: com.github.mikephil.charting.charts.Chart<*>) {
        val toEntry: (ChartElement) -> Entry
        val invalidate: () -> Unit
        val dataSet: Class<*>
        var dataSetPreference: ((DataSet<*>) -> Unit)? = null
        val chartData: ChartData<*>
        when (to) {
            is LineChart -> {
                chartData = LineData()
                dataSet = LineDataSet::class.java
                toEntry = { Entry(it.x, it.y, it) }
                invalidate = {
                    chartData.notifyDataChanged()
                    to.apply {
                        data = chartData
                        postInvalidate()
                    }
                }

                // <editor-fold desc="Appearance">
                val config = chart.configuration as LineChartConfiguration
                dataSetPreference = {
                    (it as LineDataSet).mode = config.mode.android()
                    it.setDrawCircles(config.dot)
                    it.setDrawCircleHole(config.dotHole)
                }
                config.xFormat?.let { to.xAxis.valueFormatter = it + chart }
                config.leftFormat?.let { to.axisLeft.valueFormatter = it + chart }
                config.rightFormat?.let { to.axisRight.valueFormatter = it + chart }
                // </editor-fold>
            }
            is BarChart -> {
                val bar = BarData()
                chartData = bar
                dataSet = BarDataSet::class.java
                toEntry = {
                    if (it.hasMutableValues)
                        BarEntry(it.x, it.values.toFloatArray(), it)
                    else
                        BarEntry(it.x, it.y)
                }
                invalidate = {
                    bar.notifyDataChanged()
                    to.apply {
                        data = bar
                        this.invalidate()
                    }
                }

                val config = chart.configuration as BarChartConfiguration
                to.setDrawValueAboveBar(config.drawValueAboveBar)
                to.setDrawBarShadow(config.showMaxValue)
                config.xFormat?.let { to.xAxis.valueFormatter = it + chart }
                config.leftFormat?.let { to.axisLeft.valueFormatter = it + chart }
                config.rightFormat?.let { to.axisRight.valueFormatter = it + chart }
            }
            is PieChart -> {
                val pie = PieData()
                chartData = pie
                dataSet = PieDataSet::class.java
                toEntry = { if (it.hasLabel) PieEntry(it.x, it.label) else PieEntry(it.x) }
                invalidate = {
                    pie.notifyDataChanged()
                    to.apply {
                        data = pie
                        this.invalidate()
                    }
                }

                // <editor-fold desc="Appearance">
                val config = chart.configuration as PieChartConfiguration
                to.setUsePercentValues(config.showPercentage)
                to.maxAngle = config.drawRadius
                // Center Text
                to.centerText = config.centerText.invoke()
                config.centerText.size?.let { to.setCenterTextSize(it) }
                config.centerText.color?.let { to.setCenterTextColor(it.toARGB()) }
                // </editor-fold>
            }
            else -> throw NotImplementedError()
        }

        // <editor-fold desc="Link data">
        chart.series.forEach { s ->
            val dataArray = arrayListOf<Entry>()
            s.data.forEach { element ->
                dataArray.add(toEntry(element))
            }
            val dataSetInstance = dataSet.getConstructor(List::class.java, String::class.java)
                .newInstance(dataArray, s.label.invoke()) as DataSet<Entry>
            dataSetPreference?.invoke(dataSetInstance)
            s.data.addAdditionListener { element, _ ->
                dataSetInstance.addEntry(toEntry(element))
                invalidate()
            }
            s.data.addRemovalListener { _, i ->
                dataSetInstance.removeEntry(i)
                invalidate()
            }
            (chartData::addDataSet as KCallable<*>).call(dataSetInstance)
        }
        to.tag = chart
        // </editor-fold>

        // <editor-fold desc="UI">
        to.description.apply {
            val description = chart.configuration.description
            text = description.invoke()
            description.color?.let { textColor = it.toARGB() }
            description.size?.let { textSize = it }
        }
        to.data = chartData
        to.invalidate()
        // </editor-fold>
    }

    /**
     * Adjust [chart]'s view.
     */
    private fun bind(chart: Chart, toolbar: Toolbar, view: com.github.mikephil.charting.charts.Chart<*>) {
        toolbar.title = chart.label.invoke()
        MenuInflater(toolbar.context).inflate(R.menu.chart_title_menu, toolbar.menu)

        val colorOnSurface =
            if (view.context.resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            )
                Color.WHITE
            else
                Color.BLACK

        val surfaceColor: AxisBase.() -> Unit = {
            textColor = colorOnSurface
            axisLineColor = colorOnSurface
            gridColor = colorOnSurface
        }

        view.apply {
            if (this !is PieChart)
                xAxis.apply(surfaceColor)
            legend.textColor = colorOnSurface
            if (this is BarLineChartBase<*>) {
                axisLeft.apply(surfaceColor)
                axisRight.apply(surfaceColor)
            }
        }
    }

    fun bind(chart: Chart, to: ChartHolder) {
        val viewType: Class<*> = when (chart.type) {
            ChartType.LINE -> LineChart::class.java
            ChartType.BAR -> BarChart::class.java
            ChartType.PIE -> PieChart::class.java
            else -> error("${chart.type} not implemented.")
        }
        val view =
            (if (to.holder.childCount == 1 && to.holder.children.first()::class.java == viewType)
                to.holder.children.first()
            else {
                to.holder.removeAllViews()
                (viewType.getConstructor(Context::class.java).newInstance(to.holder.context))
                    .apply { to.holder.addView(this as View) }
            }) as com.github.mikephil.charting.charts.Chart<*>
        view.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            view.context.resources.getDimensionPixelSize(R.dimen.chart_size_normal)
        )
        bind(chart, view)
        bind(chart, to.toolbar, view)
        binding[chart] = to
        to.chart = chart
    }
}