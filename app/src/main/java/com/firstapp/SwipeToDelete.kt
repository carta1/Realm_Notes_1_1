package com.firstapp.realm_notes_1_1

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

abstract class SwipeToDelete(context: Context): ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){

    private val deleteIcon = ContextCompat.getDrawable(context,R.drawable.ic_baseline_delete_24px)
    private val iconWidth = deleteIcon!!.intrinsicWidth
    private val iconHeight = deleteIcon!!.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#9b715b")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder?.itemView
        val itemHeight = itemView!!.bottom-itemView.top
        val isCancel = dX == 0f && isCurrentlyActive

        if(isCancel){
            clearCanvas(c,itemView.right + dX, itemView.top.toFloat(),itemView.right.toFloat(),itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            var currentPos = viewHolder.adapterPosition
            println("when in rome2: $currentPos")
            return
        }

        //draw the background color
        background.color = backgroundColor
        background.setBounds(itemView.right + dX.toInt(),itemView.top, itemView.right, itemView.bottom)
        background.draw(c)

        // calculate position of delete icon
        val deleteIconTop = itemView.top + (itemHeight - iconHeight) / 2
        val deleteIconMargin = (itemHeight - iconHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - iconWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + iconHeight

        //draw delete icon
        deleteIcon!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon!!.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)




    }





    private fun clearCanvas(c: Canvas?, left:Float, top: Float, right: Float, bottom: Float){
        c?.drawRect(left, top, right, bottom, clearPaint)


    }

}