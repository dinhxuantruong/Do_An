package com.example.blogapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.utils.Extension.MyButton
import java.util.LinkedList

abstract class MySwipeHelper(
    context: Context, private val recyclerView: RecyclerView,
    private var buttonWith: Int
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var bottomList: MutableList<MyButton>? = null
    private lateinit var gestureDetector: GestureDetector
    private var swipePosition = -1
    private var swipeThreshold = 0.5f
    private val buttonBuffer: MutableMap<Int, MutableList<MyButton>>

    private lateinit var removeQueue: LinkedList<Int>
    abstract fun instantiateButton(
        viewHolder: RecyclerView.ViewHolder,
        buffer: MutableList<MyButton>
    )

private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        if (bottomList != null) {
            for (button in bottomList!!) {
                if (button.onClick(e.x, e.y)) {
                    break
                }
            }
        }
        return true
    }
}


    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { _, motionEvent ->
        val position = swipePosition
        if (position == RecyclerView.NO_POSITION) return@OnTouchListener false

        val swiperViewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        val swipeItem = swiperViewHolder?.itemView
        swipeItem?.let { swipeView ->
            val rect = Rect()
            swipeView.getGlobalVisibleRect(rect)
            if (motionEvent.action == MotionEvent.ACTION_DOWN ||
                motionEvent.action == MotionEvent.ACTION_MOVE ||
                motionEvent.action == MotionEvent.ACTION_UP) {
                val point = Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
                if (rect.top < point.y && rect.bottom > point.y) {
                    gestureDetector.onTouchEvent(motionEvent)
                } else {
                    removeQueue.add(position)
                    swipePosition = RecyclerView.NO_POSITION
                    recoverSwipeItem()
                }
            }
        }
        false
    }

    private fun attachSwipe(){
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    @Synchronized
    private fun recoverSwipeItem() {
        while (!removeQueue.isEmpty()) {
            val pos = removeQueue.poll()?.toInt() ?: -1
            if (pos >= 0) {
                recyclerView.adapter?.notifyItemChanged(pos)
            }
        }
    }


    init {
        this.bottomList = ArrayList()
        this.gestureDetector = GestureDetector(context, gestureListener)
        this.recyclerView.setOnTouchListener(onTouchListener)
        this.buttonBuffer = HashMap()
        this.removeQueue = IntLinkerList()

        attachSwipe()
    }


    class IntLinkerList : LinkedList<Int>() {
        override fun contains(element: Int): Boolean {
            return false
        }

        override fun lastIndexOf(element: Int): Int {
            return element
        }

        override fun remove(element: Int): Boolean {
            return false
        }

        override fun indexOf(element: Int): Int {
            return element
        }

        override fun add(element: Int): Boolean {
            return if (contains(element))
                false
            else super.add(element)
        }
    }



    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipePosition != pos)
            removeQueue.add(swipePosition)
        swipePosition = pos
        if (buttonBuffer.containsKey(swipePosition))
            bottomList = buttonBuffer[swipePosition]
        else
            bottomList!!.clear()
        buttonBuffer.clear()
        swipeThreshold = 0.5f*bottomList!!.size.toFloat()*buttonWith.toFloat()
        recoverSwipeItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f*defaultValue
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
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipePosition = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<MyButton> = ArrayList()
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateButton(viewHolder, buffer)
                    buttonBuffer[pos] = buffer
                } else {
                    buffer = buttonBuffer[pos]!!
                }
                 translationX = dX*buffer.size.toFloat() * buttonWith.toFloat() / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
            } else {
                bottomList?.clear()
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive)
    }


    private fun drawButton(c: Canvas, itemView: View, buffer: MutableList<MyButton>, pos: Int, translationX: Float){
        var right = itemView.right.toFloat()
        val dButtonWith = -1*translationX/buffer.size
        for (button in buffer){
            val left = right - dButtonWith
            button.onDraw(c, RectF(left,itemView.top.toFloat(),right,itemView.bottom.toFloat()),pos)
            right = left
        }
        if(translationX > - 50 ){ bottomList?.clear() }
    }


}