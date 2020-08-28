package com.zhufucdev.bukkithelper.ui.clock_late

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zhufucdev.bukkithelper.R

class ClockLateFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fab: ExtendedFloatingActionButton = requireActivity().findViewById(R.id.fab_main)
        fab.setOnClickListener {
            findNavController().apply {
                previousBackStackEntry?.arguments?.putBoolean("forceAdd", true)
                navigateUp()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_clock_late, container, false)
    }
}