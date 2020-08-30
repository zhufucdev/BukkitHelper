package com.zhufucdev.bukkithelper.ui.server_selection

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.animateScale
import com.zhufucdev.bukkithelper.manager.ServerManager
import io.netty.channel.Channel

class ServerAdapter(private val fab: ExtendedFloatingActionButton) : RecyclerView.Adapter<ServerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.text_name)
        val latencyText: TextView = view.findViewById(R.id.text_latency)
        val signalIcon: AppCompatImageView = view.findViewById(R.id.icon_signal)

        private var _name: String = ""
        var name: String
            get() = _name
            set(value) {
                _name = value
                nameText.text = value
            }
        private var _latency: Int = -1
        var latency: Int
            get() = _latency
            set(value) {
                latencyText.text =
                    when (value) {
                        -1 -> latencyText.resources.getString(R.string.text_latency_testing)
                        -2 -> latencyText.resources.getString(R.string.text_connection_timeout)
                        else -> latencyText.resources.getString(R.string.title_latency, value)
                    }
                _latency = value
                signalIcon.setImageResource(
                    when (value) {
                        in -2 until 0 -> R.drawable.ic_baseline_signal_cellular_off_24
                        in 0 until 2000 -> R.drawable.ic_baseline_signal_cellular_4_bar_24
                        else -> R.drawable.ic_baseline_signal_cellular_null_24
                    }
                )
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_server_holder, parent, false))
    }

    private val handler = Handler(Looper.getMainLooper())
    private val channels = arrayListOf<Channel>()
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val server = ServerManager.servers[position]
        holder.itemView.setOnClickListener {
            mOnClickListener?.invoke(position)
        }
        holder.name = server.name
        holder.latency = -1 // Label test
        try {
            server.testLatency {
                handler.post { holder.latency = it }
            }.also {
                channels.add(it)
            }
        } catch (e: Exception) {
            holder.latencyText.text =
                holder.itemView.resources.getString(R.string.text_latency_test_failed, e::class.simpleName, e.message)
        }
    }

    override fun getItemCount(): Int = ServerManager.servers.size

    private var mOnClickListener: ((Int) -> Unit)? = null
    fun setOnClickListener(l: (Int) -> Unit) {
        mOnClickListener = l
    }

    fun closeAllChannels() {
        channels.forEach {
            it.close()
        }
    }

    fun removeAt(index: Int) {
        val deleted = ServerManager.servers[index]
        if (ServerManager.connected == deleted) deleted.disconnect()
        ServerManager.remove(deleted)
        notifyItemRemoved(index)
        // <editor-fold desc="Undo snackbar">
        Snackbar.make(fab, fab.resources.getString(R.string.text_deleted, deleted.name), (Snackbar.LENGTH_LONG * animateScale(fab.context)).toInt())
            .setAction(R.string.title_undo) {
                ServerManager.add(deleted)
                notifyItemInserted(index)
            }
            .show()
        // </editor-fold>
    }
}