package com.zhufucdev.bukkithelper.ui.api_chart

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.zhufucdev.bukkit_helper.DynamicList
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkithelper.R

/**
 * A [RecyclerView.Adapter] that works with [ChartParser].
 */
class ChartViewAdapter(private val charts: DynamicList<Chart>) : RecyclerView.Adapter<ChartViewAdapter.ChartHolder>() {

    init {
        charts.addAdditionListener { _, i ->
            notifyItemInserted(i)
        }
        charts.addRemovalListener { _, i ->
            notifyItemRemoved(i)
        }
    }

    class ChartHolder(val root: FrameLayout) : RecyclerView.ViewHolder(root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartHolder {
        return ChartHolder(
            FrameLayout(parent.context).apply {
                setPadding(context.resources.getDimensionPixelSize(R.dimen.widget_margin_normal))
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        )
    }

    override fun onBindViewHolder(holder: ChartHolder, position: Int) {
        val chart = charts[position]
        ChartParser.bind(chart, holder)
    }

    override fun getItemCount(): Int = charts.size
}