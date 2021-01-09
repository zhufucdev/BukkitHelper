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
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.isVisible
import com.zhufucdev.bukkit_helper.ui.Color
import com.zhufucdev.bukkit_helper.ui.Text
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
    val size = (textSize / resources.displayMetrics.densityDpi * DisplayMetrics.DENSITY_DEFAULT).toInt()
    return Text(text.toString(), color, size)
}

fun <T : View> ViewGroup.findViewWithType(clazz: Class<T>): T? {
    for (child in children.iterator()) {
        if (clazz.isInstance(child)) return child as T
        else if (child is ViewGroup) return child.findViewWithType(clazz) ?: continue
    }
    return null
}