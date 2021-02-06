package com.zhufucdev.bukkithelper.impl.builtin

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zhufucdev.bukkit_helper.Context
import com.zhufucdev.bukkit_helper.DynamicList
import com.zhufucdev.bukkit_helper.Plugin
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.chart.ChartElement
import com.zhufucdev.bukkit_helper.chart.Series
import com.zhufucdev.bukkit_helper.chart.ValueFormat
import com.zhufucdev.bukkit_helper.chart.configuration.LineChartConfiguration
import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.plugin.ChartPlugin
import com.zhufucdev.bukkit_helper.ui.data.Text
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.communicate.command.PlayerChangeListenCommand
import com.zhufucdev.bukkithelper.communicate.command.PlayerListCommand
import com.zhufucdev.bukkithelper.impl.Descriptor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

@Descriptor(R.string.text_player_monitor)
class PlayerMonitor : Plugin(), ChartPlugin {
    private val main = Series(DynamicList(), Text(R.string.title_player_count))
    override val chart: Chart = Chart(
        series = main,
        label = Text(R.string.title_player_monitor),
        LineChartConfiguration(
            xFormat = format,
            commonMode = LineChartConfiguration.Mode.STEPPED
        )
    )
    private val format get() = object : ValueFormat() {
        val sdf = SimpleDateFormat("dd:mm:ss", Context.locale)
        override fun axis(value: Float): String = sdf.format(Date((value + timeStart).toLong()))
    }

    private var timeStart by Delegates.notNull<Long>()
    override fun onEnable() {
        fun notifyNextPlayerChange() {
            Server.sendCommand(PlayerChangeListenCommand().apply {
                addCompleteListener {
                    if (it != null) {
                        main.data.add(
                            ChartElement(
                                (System.currentTimeMillis() - timeStart).toFloat(),
                                it.size.toFloat()
                            )
                        )
                        notifyNextPlayerChange()
                    }
                }
            })
        }

        Server.sendCommand(PlayerListCommand().apply {
            addCompleteListener {
                timeStart = System.currentTimeMillis()
                if (it != null) {
                    main.data.add(ChartElement((System.currentTimeMillis() - timeStart).toFloat(), it.size.toFloat()))
                    notifyNextPlayerChange()
                }
            }
        })
    }
}