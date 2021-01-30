package com.zhufucdev.bukkithelper.impl.chart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.zhufucdev.bukkit_helper.DynamicList
import com.zhufucdev.bukkit_helper.Implementable
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkithelper.R

/**
 * A [RecyclerView.Adapter] that works with [ChartParser].
 */
class ChartViewAdapter(private val charts: DynamicList<Chart>) : RecyclerView.Adapter<ChartHolder>() {

    init {
        charts.addAdditionListener { _, i ->
            notifyItemInserted(i)
        }
        charts.addRemovalListener { _, i ->
            notifyItemRemoved(i)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartHolder {
        return ChartHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chart_holder, parent, false),
            this
        )
    }

    override fun onBindViewHolder(holder: ChartHolder, position: Int) {
        val chart = charts[position]
        ChartParser.bind(chart, holder)

        holder.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> settingsListener?.invoke(chart) != null
                R.id.open -> openListener?.invoke(chart) != null
                else -> false
            }
        }
    }

    override fun onViewAttachedToWindow(holder: ChartHolder) {
        super.onViewAttachedToWindow(holder)
        holder.chart.markImplemented()
    }

    override fun getItemCount(): Int = charts.size

    private var settingsListener: ((Chart) -> Unit)? = null
    private var openListener: ((Chart) -> Unit)? = null

    fun setSettingsListener(l: (Chart) -> Unit) {
        settingsListener = l
    }

    fun setOpenListener(l: (Chart) -> Unit) {
        openListener = l
    }
}