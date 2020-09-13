package com.zhufucdev.bukkit_helper

import java.nio.ByteBuffer


fun Int.toByteArray(): ByteArray = ByteBuffer.allocate(4).putInt(this).array()

fun ByteArray.toInt(): Int = ByteBuffer.wrap(this).int

fun Long.toByteArray(): ByteArray = ByteBuffer.allocate(8).putLong(this).array()

fun ByteArray.toLong(): Long = ByteBuffer.wrap(this).long