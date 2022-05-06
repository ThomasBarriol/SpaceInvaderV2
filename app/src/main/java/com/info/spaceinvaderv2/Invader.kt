package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*
import java.util.*

open class Invader(context: Context, x : Float, y : Float, private val screenX: Int, screenY: Int, private val bitmap: Bitmap): draw {
    open var width: Float = screenX/ 10f
    open var height: Float = screenY / 20f

    open var position = RectF(x - width/2, y - height/2, x + width/2, y + height/2)

    var isVisible = true
    open var life = 1
    open val type = 1
    open var speedx = 200f
    open var speedy = 2f

    open fun updateMove(fps : Double, vague : Int){
        position.offset((vague) * speedx * fps.toFloat(), speedy)
        if (position.left <= 0){
            speedx *= -1
            position.offset( 20f, speedy)
        }
        else if (position.right >= screenX){
            speedx *= -1
            position.offset( -20f, speedy)
        }
    }

    fun takeShot(vague: Int): Boolean{
        val random = Random()
        var randomNumber: Int = random.nextInt(200/vague)
        return randomNumber == 0
    }
}