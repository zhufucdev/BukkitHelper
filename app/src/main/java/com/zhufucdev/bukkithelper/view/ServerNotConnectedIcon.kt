package com.zhufucdev.bukkithelper.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import com.zhufucdev.bukkithelper.R

class ServerNotConnectedIcon : FrameLayout {
    private fun inflate(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.icon_server_not_connected, this, true)
    }

    constructor(context: Context): super(context) {
        inflate(context)
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        inflate(context)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        inflate(context)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr,defStyleRes) {
        inflate(context)
    }
}