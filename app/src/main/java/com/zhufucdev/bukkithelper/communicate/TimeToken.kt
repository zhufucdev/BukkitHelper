package com.zhufucdev.bukkithelper.communicate

import com.zhufucdev.bukkit_helper.Token
import com.zhufucdev.bukkithelper.manager.ServerManager

class TimeToken(content: ByteArray, val deathTime: Long) : Token(ServerManager.LOCAL_TOKEN_HOLDER, content)