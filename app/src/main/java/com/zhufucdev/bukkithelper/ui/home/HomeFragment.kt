package com.zhufucdev.bukkithelper.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.animateScale
import com.zhufucdev.bukkithelper.fadeIn
import com.zhufucdev.bukkithelper.fadeOut
import com.zhufucdev.bukkithelper.ui.DateValueFormatter
import com.zhufucdev.bukkithelper.ui.TPSValueFormatter
import com.zhufucdev.bukkithelper.view.ServerConnectingIcon
import com.zhufucdev.bukkithelper.view.ServerNotConnectedIcon

class HomeFragment : Fragment(), NavController.OnDestinationChangedListener {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.context = requireContext()
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val navController = findNavController()

        navController.addOnDestinationChangedListener(this)

        val fab: ExtendedFloatingActionButton = requireActivity().findViewById(R.id.fab_main)
        fab.apply {
            if (text != context.getString(R.string.action_connect)) {
                hide()
            }
            setText(R.string.action_connect)
            setIconResource(R.drawable.ic_baseline_add_24)
            setBackgroundColor(context.getColor(R.color.colorAccent))
            show()
            setOnClickListener {
                navController.navigate(R.id.action_navigation_home_to_serverConnectFragment)
            }
        }
        // <editor-fold desc="Connect">
        val mainLayout: ScrollView = root.findViewById(R.id.scroll_content)
        val noConnectionIcon: ServerNotConnectedIcon = root.findViewById(R.id.icon_no_connection)
        val connectingIcon: ServerConnectingIcon = root.findViewById(R.id.icon_connecting)
        val connectionNameText: TextView = root.findViewById(R.id.text_connect_content)
        fun viewByStatus(status: HomeViewModel.ConnectionStatus) {
            when (status) {
                HomeViewModel.ConnectionStatus.CONNECTING -> {
                    mainLayout.visibility = View.GONE
                    noConnectionIcon.visibility = View.GONE
                    connectingIcon.fadeIn()
                    fab.hide()
                }
                HomeViewModel.ConnectionStatus.CONNECTED -> {
                    mainLayout.fadeIn()
                    noConnectionIcon.fadeOut()
                    connectingIcon.fadeOut()
                    fab.hide()
                }
                HomeViewModel.ConnectionStatus.DISCONNECTED -> {
                    mainLayout.fadeOut()
                    noConnectionIcon.fadeIn()
                    connectingIcon.fadeOut()
                    fab.show()
                }
            }
        }

        homeViewModel.connectionStatus.observe(viewLifecycleOwner) { status ->
            viewByStatus(status)
        }
        homeViewModel.connectionName.observe(viewLifecycleOwner) { name ->
            connectionNameText.text = requireContext().getString(R.string.text_connected_to, name)
        }
        savedInstanceState?.getInt("status")?.let { viewByStatus(HomeViewModel.ConnectionStatus.values()[it]) }
            ?: homeViewModel.connect()
        // </editor-fold>
        // <editor-fold desc="Sync data">
        val tpsChart: LineChart = root.findViewById(R.id.chart_tps)
        val playerChart: LineChart = root.findViewById(R.id.chart_players)
        tpsChart.description = Description().apply { text = requireContext().getString(R.string.title_tps) }
        tpsChart.xAxis.valueFormatter = TPSValueFormatter(requireContext())
        homeViewModel.tpsData.observe(viewLifecycleOwner) {
            tpsChart.data = it
            tpsChart.invalidate()
        }
        val dateFormat = DateValueFormatter(homeViewModel.timeStart)
        playerChart.description = Description().apply { text = requireContext().getString(R.string.title_player_online) }
        playerChart.xAxis.valueFormatter = dateFormat
        homeViewModel.playerData.observe(viewLifecycleOwner) {
            playerChart.data = it
            playerChart.invalidate()
        }
        // </editor-fold>
        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            "status",
            homeViewModel.connectionStatus.value!!.ordinal
        )
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (destination.id != R.id.navigation_home) return
        if (arguments?.getBoolean("refresh", false) == true) {
            homeViewModel.connect()
        }
    }
}