package com.zhufucdev.bukkit_helper.communicate

import com.zhufucdev.bukkit_helper.Key
import java.io.File

class FileKey: Key {
    val file: File
    val name get() = file.name
    constructor(file: File): super(file.readText()) {
        this.file = file
    }

    constructor(file: File, content: String): super(content) {
        this.file = file
    }

    fun save() {
        file.writeText(toString())
    }
}