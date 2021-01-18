package com.zhufucdev.bukkit_helper.ui.data

open class Color(val alpha: Int, val red: Int, val green: Int, val blue: Int) {
    fun toARGB(): Int {
        var s = 0
        fun add(i: Int) {
            s += i
            s = s.shl(8)
        }
        add(alpha)
        add(red)
        add(green)
        s += blue
        return s
    }
    companion object {
        val RED get() = Color(255, 255, 0, 0)
        val GREEN get() = Color(255, 0, 255, 0)
        val BLUE get() = Color(255, 0, 0, 255)
        val BLACK get() = Color(255, 0, 0, 0)
        val WHITE get() = Color(255, 255, 255, 255)

        fun fromARGB(color: Int): Color {
            val a = color.shr(24)
            val r = color.shr(16) - a.shl(8)
            val g = color.shr(8) - r.shl(8) - a.shl(16)
            val b = color - a.shl(24) - g.shl(16) - r.shl(8)
            return Color(a, r, g, b)
        }
    }
}