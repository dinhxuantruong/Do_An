package com.example.datn.utils.Extention

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.blogapp.utils.MyButtonClickListener

class MyButton(
    private val context: Context,
    private val text: String,
    private val textSize: Int,
    private val imageId: Int,
    private val color: Int,
    private val listener: MyButtonClickListener
) {

    private var pos: Int = 0
    private var clickRegion: RectF? = null
    private val resources: Resources

    init {
        resources = context.resources
    }

    fun onClick(x: Float, y: Float): Boolean {
        if (clickRegion != null && clickRegion!!.contains(x, y)) {
            listener.onClickedItem(pos)
            return true
        }
        return false
    }

    fun onDraw(c: Canvas, rectF: RectF, pos: Int) {
        val p = Paint()
        p.color = color
        c.drawRect(rectF, p)

        //Text
        p.color = Color.WHITE
        p.textSize = textSize.toFloat()


        val r = Rect()
        val cHeight = rectF.height()
        val cWidth = rectF.width()
        p.textAlign = Paint.Align.LEFT
        p.getTextBounds(text,0,text.length,r)
        var x = 0f
        var y = 0f

        if (imageId == 0){
            x = cWidth/2f - r.width()/2f  - r.left.toFloat()
            y = cHeight/2f + r.height() / 2f -r.bottom.toFloat()
            c.drawText(text,rectF.left + x , rectF.top +y, p)
        }else{
            val d = ContextCompat.getDrawable(context,imageId)
            val bitmap = drawableTobitMap(d)
            c.drawBitmap(bitmap,(rectF.left + rectF.right)/2 -35 , (rectF.top + rectF.bottom)/2 -40 ,p)
            
        }
        clickRegion = rectF
        this.pos = pos
    }


    private fun drawableTobitMap(d: Drawable?): Bitmap {
        if (d is BitmapDrawable) return  d.bitmap
        val bitmap =    Bitmap.createBitmap(d!!.intrinsicWidth,d.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        d.setBounds(0,0,canvas.width,canvas.height)
        d.draw(canvas)
        return bitmap
    }


}