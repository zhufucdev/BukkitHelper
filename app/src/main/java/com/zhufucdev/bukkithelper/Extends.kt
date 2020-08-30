package com.zhufucdev.bukkithelper

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.provider.Settings
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
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