package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*

class Mini_boss(context: Context, x : Float, y : Float, private val screenX: Int, screenY: Int, startmoving : Int, private var bitmap: Bitmap): Invader(context, x, y, screenX, screenY, bitmap ) {
    var moving = startmoving

    override var width = screenX/5f
    override var life: Int = 10
    override var height = screenY/10f
    override val type = 5

    override var position = RectF(x - width/2, y - height/2, x + width/2, y + height/2)

    override fun updateMove(fps: Double, vague : Int){
        position.offset(speedx * moving * fps.toFloat(), 0f)
        if (position.left < 0 || position.right > screenX){
            moving *= -1
        }
    }
}