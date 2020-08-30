package com.zhufucdev.bukkithelper.ui.console

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zhufucdev.bukkithelper.R

class ConsoleFragment : Fragment() {

    private lateinit var consoleViewModel: ConsoleViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        consoleViewModel =
                ViewModelProvider(this).get(ConsoleViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_console, container, false)
        return root
    }
}