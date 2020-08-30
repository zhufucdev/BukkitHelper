package com.zhufucdev.bukkithelper.ui

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class DateValueFormatter(val timeStart: Long) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val current = timeStart + value.roundToLong()
        return SimpleDateFormat("kk:mm:ss", Locale.getDefault()).format(Date(current))
    }
}