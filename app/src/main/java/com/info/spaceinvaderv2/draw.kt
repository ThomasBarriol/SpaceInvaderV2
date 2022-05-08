package com.info.spaceinvaderv2

import android.graphics.*

interface drawable{

    fun draw(canvas: Canvas, bitmap: Bitmap, position : RectF, paint : Paint){
        canvas.drawBitmap(bitmap, position.left, position.top, paint)
    }
}