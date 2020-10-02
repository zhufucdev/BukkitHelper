package com.zhufucdev.bukkit_helper

import io.netty.buffer.ByteBuf
import java.nio.ByteBuffer


fun Int.toByteArray(): ByteArray = ByteBuffer.allocate(4).putInt(this).array()

fun ByteArray.toInt(): Int = ByteBuffer.wrap(this).int

fun Long.toByteArray(): ByteArray = ByteBuffer.allocate(8).putLong(this).array()

fun ByteArray.toLong(): Long = ByteBuffer.wrap(this).long

fun Double.toByteArray(): ByteArray = ByteBuffer.allocate(8).putDouble(this).array()

fun ByteArray.toDouble(): Double = ByteBuffer.wrap(this).double

fun ByteBuf.readIInt(): Int {
    val buf = ByteArray(4)
    readBytes(buf, 0, 4)
    return buf.toInt()
}