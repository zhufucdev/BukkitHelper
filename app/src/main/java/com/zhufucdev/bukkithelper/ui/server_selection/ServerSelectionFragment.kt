package com.zhufucdev.bukkithelper.ui.server_selection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.manager.ServerManager

class ServerSelectionFragment : Fragment(), NavController.OnDestinationChangedListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.server_selection_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val navigator = findNavController()

        navigator.addOnDestinationChangedListener(this)

        val swipeRefresh: SwipeRefreshLayout = requireView() as SwipeRefreshLayout
        val recyclerView: RecyclerView = requireView().findViewById(R.id.recycler_server_list)
        val fab: ExtendedFloatingActionButton = requireActivity().findViewById(R.id.fab_main)
        val adapter = ServerAdapter(fab)
        val itemTouchHelperCallBack = SwipeToDeleteCallBack(adapter, requireContext())

        itemTouchHelperCallBack.setDeleteListener { navigator.previousBackStackEntry?.arguments?.putBoolean("refresh", true) }
        ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnClickListener {
            ServerManager.default = ServerManager.servers[it]
            navigator.apply {
                previousBackStackEntry?.arguments?.putBoolean("refresh", true)
                navigateUp()
            }
        }

        swipeRefresh.setOnRefreshListener {
            adapter.closeAllChannels()
            adapter.notifyDataSetChanged()
            swipeRefresh.isRefreshing = false
        }

        fab.apply {
            hide()
            setText(R.string.title_add_server)
            setIconResource(R.drawable.ic_baseline_add_24)
            setBackgroundColor(context.getColor(R.color.colorAccent))
            show()
            setOnClickListener {
                navigator.navigate(R.id.action_serverSelection_to_navigation_server_connect)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        findNavController().removeOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (destination.id != R.id.navigate_server_selection || arguments == null) return
        if (arguments.getBoolean("refresh", false)) {
            controller.previousBackStackEntry?.arguments?.putBoolean("refresh", true)
        }
    }
}