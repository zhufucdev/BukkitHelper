package com.zhufucdev.bukkit_helper.util

import com.zhufucdev.bukkit_helper.MainPlugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask

object TPSMonitor {
    private var lastRoundTime = 0
    private var task: BukkitTask? = null
    fun start() {
        task?.cancel()

        var lastTimeMilli = System.currentTimeMillis()
        var count = 0
        task = Bukkit.getScheduler().runTaskTimer(MainPlugin.default, Runnable {
            count ++
            if (count >= 20) {
                count = 0
                lastRoundTime = (System.currentTimeMillis() - lastTimeMilli).toInt()
                lastTimeMilli = System.currentTimeMillis()
            }
        }, 0, 1)
    }

    fun stop() {
        task?.cancel()
    }

    val value: Double get() = 1000.0 / lastRoundTime * 20
}