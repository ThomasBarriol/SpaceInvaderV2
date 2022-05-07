package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*
import java.util.*

open class Invader(x : Float, y : Float, private val screenX: Int, private val screenY: Int, private val w: Int, private val h: Int, InitMoving : Int): draw {
    open var width = w
    open var height = h

    open var position = RectF(x - width/2, y - height/2, x + width/2, y + height/2)

    var isVisible = true
    var moving = InitMoving
    open var life: Int = 1
    open val type: Int = 1
    open var speedx: Float = 200f
    open var speedy: Float = 2f

    fun updateMove(fps : Double, vague : Int){
        position.offset((vague) * speedx * moving * fps.toFloat(), speedy)
        if (position.left <= 0){
            moving *= -1
            position.offset( 20f, speedy)
        }
        else if (position.right >= screenX){
            moving *= -1
            position.offset( -20f, speedy)
        }
    }

    fun takeShot(): Boolean{
        val random = Random()
        var randomNumber: Int = random.nextInt(200)
        return (randomNumber == 0 && position.top >= 0)
    }
}