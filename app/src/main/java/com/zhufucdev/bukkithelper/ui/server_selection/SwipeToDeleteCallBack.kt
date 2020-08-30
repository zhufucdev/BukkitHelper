package com.zhufucdev.bukkithelper.ui.server_selection

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.zhufucdev.bukkithelper.R

class SwipeToDeleteCallBack(private val adapter: ServerAdapter, context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)!!
    private val background = ColorDrawable(context.getColor(R.color.red))

    private var mDeleteListener: ((Int) -> Unit)? = null
    fun setDeleteListener(l: (Int) -> Unit) {
        mDeleteListener = l
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val index = viewHolder.adapterPosition
        adapter.removeAt(index)
        mDeleteListener?.invoke(index)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val view = viewHolder.itemView
        val bgOffset = 20
        val iconMargin = (view.height - icon.intrinsicHeight) / 2
        val iconTop = view.top + iconMargin
        val iconBottom = iconTop + icon.intrinsicHeight
        when {
            dX > 0 -> {
                background.setBounds(view.left, view.top, view.left + dX.toInt() + bgOffset, view.bottom)
                icon.setBounds(
                    view.left + iconMargin,
                    iconTop,
                    view.left + iconMargin + icon.intrinsicWidth,
                    iconBottom
                )
            }
            dX < 0 -> {
                background.setBounds(view.right + dX.toInt() - bgOffset, view.top, view.right, view.bottom)
                icon.setBounds(
                    view.right - iconMargin - icon.intrinsicWidth,
                    iconTop,
                    view.right - iconMargin,
                    iconBottom
                )
            }
            else -> {
                return
            }
        }
        background.draw(c)
        icon.draw(c)
    }
}