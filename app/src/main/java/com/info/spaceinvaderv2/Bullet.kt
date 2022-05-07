package com.info.spaceinvaderv2

import android.graphics.*

open class Bullet(private val screenY: Int, x : Float, y: Float, private val direction : Int, private val speed : Float = 800f, heightModifier: Float = 30f) {

    private var paint: Paint = Paint()

    private val width = 10
    private val height = screenY / heightModifier
    open val position = RectF(x - width/2, y , x + width/2, y + height)

    var isActive = true

    open fun draw(canvas: Canvas){
        paint.color = Color.WHITE
        canvas.drawRect(position, paint)
    }

    open fun update(fps: Double) {
        position.offset(0f,speed * direction *fps.toFloat())
        if (position.top < 0 || position.bottom > screenY){
            isActive = false
        }
    }

}