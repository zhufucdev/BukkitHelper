package com.zhufucdev.bukkithelper.ui.connect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.communicate.PreferenceKey
import com.zhufucdev.bukkithelper.manager.KeyManager

class KeyAdapter(private val context: Context) : BaseAdapter(), Filterable {
    private var mItemClickListener: ((PreferenceKey?, Int) -> Unit)? = null

    /**
     * Set a listener for item click.
     * @param l Called every time an item is click. If "Create Key" item is clicked, the Key is null.
     */
    fun setItemClickListener(l: (PreferenceKey?, Int) -> Unit) {
        this.mItemClickListener = l
    }

    override fun getCount(): Int = KeyManager.keys.size + 1

    override fun getItem(p0: Int): Any = if (p0 < KeyManager.keys.size) KeyManager.keys[p0].name else context.getString(R.string.title_new_key)

    override fun getItemId(p0: Int): Long =
        if (p0 < KeyManager.keys.size) KeyManager.keys[p0].hashCode().toLong() else 1

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        return if (p0 < KeyManager.keys.size)
            inflater.inflate(R.layout.dropdown_key_item, p2, false).apply {
                isClickable = true
                val key = KeyManager.keys[p0]
                val textView: TextView = findViewById(R.id.text_key_name)
                textView.text = key.name
                setOnClickListener { mItemClickListener?.invoke(key, p0) }
            }
        else
            inflater.inflate(R.layout.dropdown_creatre_item, p2, false).apply {
                isClickable = true
                setOnClickListener { mItemClickListener?.invoke(null, p0) }
            }
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            return FilterResults()
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
        }
    }
}