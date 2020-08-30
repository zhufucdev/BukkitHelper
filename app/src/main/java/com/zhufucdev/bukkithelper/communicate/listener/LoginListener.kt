package com.zhufucdev.bukkithelper.communicate.listener

import com.zhufucdev.bukkithelper.communicate.LoginResult

interface LoginListener {
    fun invoke(result: LoginResult)
}