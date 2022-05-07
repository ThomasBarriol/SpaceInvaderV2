package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*

class Mini_boss(x : Float, y : Float, private val screenX: Int, screenY: Int, startmoving : Int, w : Int, h : Int): Invader(x, y, screenX, screenY, w, h), draw {
    var moving = startmoving

    override var width = w
    override var life: Int = 10
    override var height = h
    override val type: Int = 5

    override var position = RectF(x - width/2, y - height/2, x + width/2, y + height/2)

    override fun updateMove(fps: Double, vague : Int){
        position.offset(speedx * moving * fps.toFloat(), speedy)
        if (position.left < 0 || position.right > screenX){
            moving *= -1
        }
    }
}