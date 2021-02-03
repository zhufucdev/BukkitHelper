package com.zhufucdev.bukkithelper

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.core.view.isVisible
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.zhufucdev.bukkit_helper.chart.Chart
import com.zhufucdev.bukkit_helper.chart.ValueFormat
import com.zhufucdev.bukkit_helper.chart.configuration.LineChartConfiguration
import com.zhufucdev.bukkit_helper.chart.configuration.LineChartConfiguration.Mode.*
import com.zhufucdev.bukkit_helper.ui.data.Color
import com.zhufucdev.bukkit_helper.ui.data.Gravity
import com.zhufucdev.bukkit_helper.ui.data.Space
import com.zhufucdev.bukkit_helper.ui.data.Text
import com.zhufucdev.bukkithelper.impl.chart.ChartFormatter
import kotlin.math.roundToInt
import kotlin.math.roundToLong

fun animateScale(context: Context): Float =
    Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1F)

private val lastAnimator = hashMapOf<View, ValueAnimator>()
fun View.fadeIn() {
    lastAnimator[this]?.cancel()
    if (!isVisible) {
        alpha = 0F
        visibility = View.VISIBLE
    }
    ObjectAnimator.ofFloat(alpha, 1F).apply {
        duration = (300 * animateScale(context)).roundToLong()
        addUpdateListener {
            alpha = it.animatedValue as Float
        }
        doOnEnd {
            lastAnimator.remove(this@fadeIn)
        }
        lastAnimator[this@fadeIn] = this
        start()
    }
}

fun View.fadeOut() {
    lastAnimator[this]?.cancel()
    if (!isVisible) return
    ObjectAnimator.ofFloat(alpha, 0F).apply {
        duration = (300 * animateScale(context)).roundToLong()
        addUpdateListener {
            alpha = it.animatedValue as Float
        }
        doOnEnd {
            lastAnimator.remove(this@fadeOut)
            visibility = View.GONE
        }
        lastAnimator[this@fadeOut] = this
        start()
    }
}

fun TextView.getMText(replacement: String = ""): Text {
    val text = if (replacement.isEmpty()) this.text else replacement
    val color = Color.fromARGB(currentTextColor)
    val size = convertUnit(textSize, LengthUnit.Dp)
    return Text(text.toString(), color, size)
}

fun <T : View> ViewGroup.findViewWithType(clazz: Class<T>): T? {
    for (child in children.iterator()) {
        if (clazz.isInstance(child)) return child as T
        else if (child is ViewGroup) return child.findViewWithType(clazz) ?: continue
    }
    return null
}

fun Gravity.android() = when (this) {
    Gravity.START -> android.view.Gravity.START
    Gravity.END -> android.view.Gravity.END
    Gravity.CENTER -> android.view.Gravity.CENTER
    Gravity.CENTER_VERTICAL -> android.view.Gravity.CENTER_VERTICAL
    Gravity.CENTER_HORIZONTAL -> android.view.Gravity.CENTER_HORIZONTAL
}

enum class LengthUnit {
    Px, Dp
}

fun convertUnit(source: Float, target: LengthUnit, densityDpi: Int): Float =
    if (target == LengthUnit.Px)
        source * densityDpi / DisplayMetrics.DENSITY_DEFAULT
    else
        source / densityDpi * DisplayMetrics.DENSITY_DEFAULT

fun View.convertUnit(source: Float, target: LengthUnit): Float {
    val ddpi = resources.displayMetrics.densityDpi
    return convertUnit(source, target, ddpi)
}

fun Space.convertToPx(context: Context): Space {
    val ddpi = context.resources.displayMetrics.densityDpi
    return Space(
        top = convertUnit(top.toFloat(), LengthUnit.Px, ddpi).roundToInt(),
        bottom = convertUnit(bottom.toFloat(), LengthUnit.Px, ddpi).roundToInt(),
        start = convertUnit(start.toFloat(), LengthUnit.Px, ddpi).roundToInt(),
        end = convertUnit(end.toFloat(), LengthUnit.Px, ddpi).roundToInt()
    )
}

operator fun ValueFormat.plus(chart: Chart): ValueFormatter = ChartFormatter(chart, this)

fun LineChartConfiguration.Mode.android(): LineDataSet.Mode = when (this) {
    CUBIC_BEZIER -> LineDataSet.Mode.CUBIC_BEZIER
    STEPPED -> LineDataSet.Mode.STEPPED
    LINEAR -> LineDataSet.Mode.LINEAR
}
