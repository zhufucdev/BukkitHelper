package com.zhufucdev.bukkithelper.ui.connect

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.animateScale
import com.zhufucdev.bukkithelper.communicate.LoginResult
import com.zhufucdev.bukkithelper.communicate.PreferenceKey
import com.zhufucdev.bukkithelper.communicate.Server
import com.zhufucdev.bukkithelper.manager.KeyManager
import com.zhufucdev.bukkithelper.manager.ServerManager
import kotlin.math.roundToLong

class ServerConnectFragment : Fragment(), NavController.OnDestinationChangedListener {
    private lateinit var keyEdit: AutoCompleteTextView
    private lateinit var addressLayout: TextInputLayout

    private lateinit var navigator: NavController

    private var key: Key? = null
    private var mForceAdd = false
    private var forceAdd: Boolean
        get() = mForceAdd
        set(value) {
            if (value) {
                fab.setBackgroundColor(requireContext().getColor(R.color.red))
                fab.setText(R.string.action_force_add)
                fab.setIconResource(R.drawable.ic_baseline_add_24)
            } else {
                fab.setBackgroundColor(requireContext().getColor(R.color.colorAccent))
                fab.setText(R.string.action_done)
                fab.setIconResource(R.drawable.ic_baseline_done_24)
            }
            mForceAdd = value
        }
    private var lastServer: Server? = null

    private val fab: ExtendedFloatingActionButton by lazy { requireActivity().findViewById(R.id.fab_main) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navigator = findNavController()
        val viewModel = ServerConnectViewModel()

        val root = inflater.inflate(R.layout.fragment_server_connect, container, false)
        // <editor-fold desc="Key Selection">
        keyEdit = root.findViewById(R.id.edit_key)
        val keyAdapter = KeyAdapter(requireContext())
        keyAdapter.setItemClickListener { key, index ->
            if (key == null) {
                navigator.navigate(R.id.action_serverConnectFragment_to_keyEditFragment)
            } else {
                this.key = key
                keyEdit.setText(key.name)
            }
        }
        navigator.addOnDestinationChangedListener(this)
        keyEdit.setAdapter(keyAdapter)
        // </editor-fold>
        // <editor-fold desc="Info Validation">
        val nameEdit: TextInputEditText = root.findViewById(R.id.edit_name)
        val nameLayout: TextInputLayout = root.findViewById(R.id.edit_name_layout)
        val addressEdit: TextInputEditText = root.findViewById(R.id.edit_address)
        addressLayout = root.findViewById(R.id.edit_address_layout)
        val keyLayout: TextInputLayout = root.findViewById(R.id.edit_key_layout)

        nameEdit.doAfterTextChanged {
            nameLayout.error =
                if (it.isNullOrEmpty()) requireContext().getString(R.string.text_content_empty)
                else null
        }
        addressEdit.doAfterTextChanged {
            addressLayout.error =
                when {
                    it.isNullOrEmpty() -> requireContext().getString(R.string.text_content_empty)
                    ServerManager[it.toString()] != null -> requireContext().getString(R.string.text_server_exists)
                    else -> {
                        if (forceAdd) {
                            fab.hide()
                            forceAdd = false
                            fab.show()
                        }
                        null
                    }
                }
        }
        keyEdit.doAfterTextChanged {
            keyLayout.error = null
            if (forceAdd) {
                fab.hide()
                forceAdd = false
                fab.show()
            }
        }
        // </editor-fold>
        fab.apply {
            hide()
            forceAdd = false
            show()
        }
        fab.setOnClickListener {
            // <editor-fold desc="Redo info validation" defaultstate="collapsed">
            val name = nameEdit.text?.toString()
            if (name.isNullOrEmpty()) {
                nameLayout.error = requireContext().getString(R.string.text_content_empty)
                return@setOnClickListener
            }
            val address = addressEdit.text?.toString()
            if (address.isNullOrEmpty()) {
                addressLayout.error = requireContext().getString(R.string.text_content_empty)
                return@setOnClickListener
            }
            if (ServerManager[address] != null) {
                addressLayout.error = requireContext().getString(R.string.text_server_exists)
                return@setOnClickListener
            }
            val key = keyEdit.text?.toString()
            if (key.isNullOrEmpty()) {
                keyLayout.error = requireContext().getString(R.string.text_content_empty)
                return@setOnClickListener
            }
            // </editor-fold>
            // <editor-fold desc="Collect info">
            var host = address
            var port = 8080
            val index = address.indexOf(':')
            if (index != -1) {
                host = address.substring(0, index)
                val t = address.substring(index + 1).toIntOrNull()
                if (t == null) {
                    addressLayout.error = requireContext().getString(R.string.text_invalidate_address)
                    return@setOnClickListener
                }
                port = t
            }
            val keyTemp = KeyManager[key]
            if (keyTemp == null) {
                keyLayout.error = requireContext().getString(R.string.text_key_not_exists)
                return@setOnClickListener
            }
            // </editor-fold>
            // <editor-fold desc="Connect to new server">
            val server = Server(name, host, port, keyTemp)
            lastServer = server

            if (!forceAdd) {
                viewModel.tryConnecting(server) {
                    when (it) {
                        LoginResult.SUCCESS -> {
                            backWithSuccess()
                            forceAdd = false
                        }
                        LoginResult.CONNECTION_FAILED -> {
                            ServerManager.remove(server)
                            addressLayout.error = requireContext().getString(R.string.text_connection_failed)
                            forceAdd = true
                        }
                        LoginResult.TIME -> {
                            ServerManager.remove(server)
                            navigator.navigate(R.id.action_serverConnectFragment_to_clockLateFragment)
                            forceAdd = true
                        }
                        LoginResult.FAILED -> {
                            ServerManager.remove(server)
                            keyLayout.error = requireContext().getString(R.string.text_wrong_key)
                            forceAdd = true
                        }
                    }
                }

                viewModel.isConnecting.observe(viewLifecycleOwner) { connecting ->
                    val block: View = root.findViewById(R.id.group_block)
                    if (connecting) {
                        fab.hide()
                        ObjectAnimator.ofFloat(0F, 0.7F).apply {
                            duration = (300 * animateScale(requireContext())).roundToLong()
                            doOnStart {
                                block.alpha = 0F
                                block.visibility = View.VISIBLE
                            }
                            addUpdateListener {
                                block.alpha = it.animatedValue as Float
                            }
                            start()
                        }
                    } else {
                        fab.show()
                        ObjectAnimator.ofFloat(0.7F, 0F).apply {
                            duration = (300 * animateScale(requireContext())).roundToLong()
                            doOnEnd {
                                block.visibility = View.GONE
                            }
                            addUpdateListener { animator ->
                                block.alpha = animator.animatedValue as Float
                            }
                            start()
                        }
                    }
                }
            } else {
                forceAdd(server)
            }
            // </editor-fold>
        }
        return root
    }

    private fun backWithSuccess() {
        navigator.previousBackStackEntry?.arguments?.putBoolean("refresh", true)
        navigator.navigateUp()
    }

    private fun forceAdd(server: Server) {
        ServerManager.add(server)
        backWithSuccess()
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (arguments == null || destination.id != R.id.navigation_server_connect) {
            return
        }
        if (arguments.containsKey("keyName")) {
            val key = PreferenceKey(arguments.getString("keyName")!!, arguments.getString("content")!!)
            if (this.key == key) return
            KeyManager.add(key)
            this.key = key
            (keyEdit.adapter as KeyAdapter).notifyDataSetChanged()
            keyEdit.setText(key.name)
        } else if (arguments.containsKey("forceAdd")) {
            val server = lastServer ?: return
            forceAdd(server)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        findNavController().removeOnDestinationChangedListener(this)
    }
}