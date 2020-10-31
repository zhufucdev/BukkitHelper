package com.zhufucdev.bukkithelper.impl.builtin

import com.zhufucdev.bukkit_helper.Context
import com.zhufucdev.bukkit_helper.DynamicList
import com.zhufucdev.bukkit_helper.DynamicRefreshInterval
import com.zhufucdev.bukkit_helper.Plugin
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.chart.ChartElement
import com.zhufucdev.bukkit_helper.chart.Series
import com.zhufucdev.bukkit_helper.chart.ValueFormat
import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.plugin.ChartPlugin
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.communicate.command.TPSFetchCommand
import com.zhufucdev.bukkithelper.manager.DataRefreshDelay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer

@Descriptor(R.string.text_tps)
class PlayerCounter: Plugin(), ChartPlugin {
    private val main: Series = Series(DynamicList(), Context.getString(R.string.title_tps))
    override val chart: Chart = Chart(main).apply { xFormat = format }

    private var tpsTask: Timer? = null
    private var timeStart = System.currentTimeMillis()
    override fun onEnable() {
        super.onEnable()

        timeStart = System.currentTimeMillis()
        fun timeElapsed(): Long = System.currentTimeMillis() - timeStart

        fun applyTpsTask() {
            tpsTask?.cancel()
            tpsTask =
                fixedRateTimer(
                    name = "TPS-Task",
                    period = DataRefreshDelay[DynamicRefreshInterval.KnownName.TPS].toLong()
                ) {
                    val command = TPSFetchCommand()
                    command.addCompleteListener {
                        if (it == null) return@addCompleteListener
                        main.data.add(ChartElement(timeElapsed() / 1000F, it.toFloat()))
                    }
                    Server.sendCommand(command)
                }
        }
        applyTpsTask()
        // Listen changes
        DataRefreshDelay.addDelayChangeListener(DynamicRefreshInterval.KnownName.TPS) { applyTpsTask() }
    }

    private val format = object : ValueFormat() {
        val sdf = SimpleDateFormat("kk:mm:ss", Context.locale)
        override fun axis(value: Float): String {
            val receiveTime = (value * 1000.0).toInt() + timeStart
            return sdf.format(Date(receiveTime))
        }
    }
}