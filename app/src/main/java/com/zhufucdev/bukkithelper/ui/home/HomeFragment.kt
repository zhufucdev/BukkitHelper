package com.zhufucdev.bukkithelper.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zhufucdev.bukkithelper.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, {
        })

        val navController = findNavController()

        val fab: ExtendedFloatingActionButton = requireActivity().findViewById(R.id.fab_main)
        fab.apply {
            setText(R.string.action_connect)
            setIconResource(R.drawable.ic_baseline_add_24)
            setOnClickListener {
                navController.navigate(R.id.action_navigation_home_to_serverConnectFragment)
            }
        }

        return root
    }
}