package com.info.spaceinvaderv2

import android.graphics.*
import kotlin.math.*

class BulletMiniBoss(private val screenY: Int, x : Float, y: Float, private val heading : Int, private val speed : Float = 800f, heightModifier: Float = 30f, private var direction: Float): Bullet(screenY, x, y, heading, speed, heightModifier) {
    private var paint: Paint = Paint()

    private val radius: Float = 20f
    override val position = RectF(x - radius, y - radius, x+radius, y+radius)
    private var dx = sin(direction)*speed
    private var dy = cos(direction)*speed

    override fun draw(canvas: Canvas){
        paint.color = Color.RED
        canvas.drawOval(position, paint)
    }

    override fun update(fps: Double) {
        position.offset(dx*fps.toFloat(), dy*fps.toFloat())
        if (position.top < 0 || position.bottom > screenY)
            isActive = false
    }
}