package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*

class Miniboss(x : Float, y : Float, private val screenX: Int, screenY: Int, startmoving : Int, w : Int, h : Int): Invader(x, y, screenX, screenY, w, h, startmoving), draw {

    override var width = w
    override var life: Int = 10
    override var height = h
    override val type: Int = 5
    override var speedx: Float = 200f

    override var position = RectF(x - width/2, y - height/2, x + width/2, y + height/2)

    override fun updateMove(fps: Double, vague: Int) {
        position.offset(speedx * moving * fps.toFloat(), 0f)
        if (position.left <= 0){
            moving *= -1
            position.offset( 20f, 0f)
        }
        else if (position.right >= screenX){
            moving *= -1
            position.offset( -20f, 0f)
        }
    }

}