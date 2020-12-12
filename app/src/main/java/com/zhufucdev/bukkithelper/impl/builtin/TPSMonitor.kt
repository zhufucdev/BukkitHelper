package com.zhufucdev.bukkithelper.impl.builtin

import com.zhufucdev.bukkit_helper.Context
import com.zhufucdev.bukkit_helper.DynamicList
import com.zhufucdev.bukkit_helper.DynamicRefreshInterval
import com.zhufucdev.bukkit_helper.Plugin
import com.zhufucdev.bukkit_helper.chart.*
import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.plugin.ChartPlugin
import com.zhufucdev.bukkit_helper.plugin.UIPlugin
import com.zhufucdev.bukkit_helper.ui.*
import com.zhufucdev.bukkit_helper.ui.component.TextFrame
import com.zhufucdev.bukkit_helper.ui.layout.LinearLayout
import com.zhufucdev.bukkit_helper.workflow.Link
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.communicate.command.TPSFetchCommand
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer

@Descriptor(R.string.text_tps)
class TPSMonitor : Plugin(), ChartPlugin, UIPlugin {
    /* Chart */
    private val main: Series = Series(DynamicList(), Text(R.string.title_tps))
    override val chart: Chart = Chart(main, ChartType.LINE, Text(R.string.title_tps)).apply { xFormat = format }

    /* UI */
    val container: GroupComponent = LinearLayout(
        TextFrame(Text(R.string.app_name), color = Color.BLUE, size = 24),
        TextFrame(Text("is great.")),
        gravity = LinearLayout.Gravity.CENTER,
        width = Component.MATCH_PARENT
    )
    override val ui: UserInterface = UserInterface(Text(R.string.title_tps), container).apply {
        Link.builder()
            .from(chart).to(this)
            .build()
    }

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
                    period = Context.dynamicRefreshInterval[DynamicRefreshInterval.KnownName.TPS].toLong()
                ) {
                    val command = TPSFetchCommand()
                    command.addCompleteListener {
                        if (it == null) return@addCompleteListener
                        main.data.add(ChartElement(timeElapsed() / 1000F, it.toFloat()))
                        if (main.data.size > 1000) {
                            // Boundary is 1000
                            main.data.removeAt(0)
                        }
                    }
                    Server.sendCommand(command)
                }
        }
        applyTpsTask()
        // Listen changes
        Context.dynamicRefreshInterval.addDelayChangeListener(DynamicRefreshInterval.KnownName.TPS) { applyTpsTask() }
    }

    override fun onDisable() {
        super.onDisable()

        tpsTask?.cancel()
        tpsTask = null
    }

    private val format
        get() = object : ValueFormat() {
            val sdf = SimpleDateFormat("kk:mm:ss", Context.locale)
            override fun axis(value: Float): String {
                val receiveTime = (value * 1000.0).toInt() + timeStart
                return sdf.format(Date(receiveTime))
            }
        }
}