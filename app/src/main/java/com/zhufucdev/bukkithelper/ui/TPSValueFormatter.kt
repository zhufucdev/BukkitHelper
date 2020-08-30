package com.zhufucdev.bukkithelper.ui

import android.content.Context
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.zhufucdev.bukkithelper.R
import java.text.DecimalFormat

class TPSValueFormatter(private val context: Context) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return context.getString(R.string.title_unit_s, DecimalFormat("#.#").format(value))
    }

    override fun getPointLabel(entry: Entry): String {
        return DecimalFormat("#.##").format(entry.y)
    }
}