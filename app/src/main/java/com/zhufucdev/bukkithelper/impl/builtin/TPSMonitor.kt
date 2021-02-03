package com.zhufucdev.bukkithelper.impl.builtin

import com.zhufucdev.bukkit_helper.*
import com.zhufucdev.bukkit_helper.chart.*
import com.zhufucdev.bukkit_helper.chart.configuration.LineChartConfiguration
import com.zhufucdev.bukkit_helper.communicate.Server
import com.zhufucdev.bukkit_helper.plugin.ChartPlugin
import com.zhufucdev.bukkit_helper.plugin.UIPlugin
import com.zhufucdev.bukkit_helper.ui.*
import com.zhufucdev.bukkit_helper.ui.component.Button
import com.zhufucdev.bukkit_helper.ui.component.TextEdit
import com.zhufucdev.bukkit_helper.ui.component.TextFrame
import com.zhufucdev.bukkit_helper.ui.data.Color
import com.zhufucdev.bukkit_helper.ui.data.Gravity
import com.zhufucdev.bukkit_helper.ui.data.Space
import com.zhufucdev.bukkit_helper.ui.data.Text
import com.zhufucdev.bukkit_helper.ui.layout.LinearLayout
import com.zhufucdev.bukkit_helper.workflow.Execute
import com.zhufucdev.bukkit_helper.workflow.Link
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.communicate.command.TPSFetchCommand
import com.zhufucdev.bukkithelper.impl.Descriptor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer

@Descriptor(R.string.text_tps)
class TPSMonitor : Plugin(), ChartPlugin, UIPlugin {
    /* Chart */
    private val main: Series = Series(DynamicList(), Text(R.string.title_tps))
    override val chart: Chart = Chart(
        series = main,
        label = Text(R.string.title_tps),
        LineChartConfiguration(
            xFormat = format,
            mode = LineChartConfiguration.Mode.CUBIC_BEZIER
        )
    )

    /* UI */
    val title = TextFrame(Text(R.string.app_name, Color.BLUE, size = 24F))
    val textFrame = TextFrame(Text.EMPTY)
    val textEdit = TextEdit(initialText = Text("is great.")).apply {
        Link.builder()
            .from(this)
            .to(TextEdit.ContentChanged {
                textFrame.text = it.after
            })
            .build()
        width = Component.MATCH_PARENT
    }
    val container: GroupComponent = LinearLayout(
        title,
        textFrame,
        textEdit,
        Button(Text("Yes")).apply {
            Link.builder()
                .from(this)
                .to(Execute {
                    textFrame.text = textEdit.content.format(color = Color.GREEN)
                })
                .build()
        },
        gravity = Gravity.CENTER,
        padding = Space.all(12)
    ).apply {
        width = Component.MATCH_PARENT
        layoutGravity[title] = Gravity.START
        layoutGravity[textFrame] = Gravity.END
        margin[textFrame] = Space(end = 120)
    }
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