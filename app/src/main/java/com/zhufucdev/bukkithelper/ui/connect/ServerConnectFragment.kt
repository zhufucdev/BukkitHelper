package com.zhufucdev.bukkithelper.ui.connect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.communicate.PreferenceKey
import com.zhufucdev.bukkithelper.manager.KeyManager

class ServerConnectFragment : Fragment(), NavController.OnDestinationChangedListener {
    private lateinit var keySpinner: AutoCompleteTextView

    private var key: Key? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navigator = findNavController()

        val root = inflater.inflate(R.layout.fragment_server_connect, container, false)
        // <editor-fold desc="Key Selection">
        keySpinner  = root.findViewById(R.id.edit_key)
        val keyAdapter = KeyAdapter(requireContext())
        keyAdapter.setItemClickListener { key, index ->
            if (key == null) {
                navigator.navigate(R.id.action_serverConnectFragment_to_keyEditFragment)
            } else {
                this.key = key
                keySpinner.setText(key.name)
            }
        }
        navigator.addOnDestinationChangedListener(this)
        keySpinner.setAdapter(keyAdapter)
        // </editor-fold>

        val fab : ExtendedFloatingActionButton = requireActivity().findViewById(R.id.fab_main)
        fab.apply {
            hide()
            setText(R.string.action_done)
            setIconResource(R.drawable.ic_baseline_done_24)
            show()
        }
        fab.setOnClickListener {

        }
        return root
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (arguments == null || !arguments.containsKey("keyName")) {
            return
        }
        val key = PreferenceKey(arguments.getString("keyName")!!, arguments.getString("content")!!)
        if (this.key == key) return
        KeyManager.add(key)
        this.key = key
        (keySpinner.adapter as KeyAdapter).notifyDataSetChanged()
        keySpinner.setSelection(KeyManager.keys.lastIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        findNavController().removeOnDestinationChangedListener(this)
    }
}