package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*
import java.util.*
import kotlin.math.atan

open class Invader(x : Float, y : Float, private val screenX: Int, private val screenY: Int, private val w: Int, private val h: Int, InitMoving : Int): drawable{
    open var width = w
    open var height = h
    val random : Random = Random()

    open var position = RectF(x - width/2, y - height/2, x + width/2, y + height/2)

    var isVisible = true
    var moving = InitMoving
    open var life: Int = 1
    open var type: Int = 1
    open var speedx: Float = (100..300).random().toFloat()
    private var speedy: Float = (100..200).random().toFloat()

    open fun updateMove(fps : Double, vague : Int){
        val multiplier = if (vague <= 4) vague else 4
        var changeDirectionalityX: Boolean = random.nextInt(200/multiplier) == 0
        var changeDirectionalityY: Boolean = random.nextInt(50/multiplier) == 0
        moving = if(changeDirectionalityX) moving*-1 else moving
        var movingY = if(changeDirectionalityY) moving*-1 else 1
        position.offset((multiplier) * speedx * moving * fps.toFloat(),  movingY * speedy*fps.toFloat())
        if (position.left <= 0){
            moving *= -1
            position.offset( 20f, speedy*fps.toFloat())
        }
        else if (position.right >= screenX){
            moving *= -1
            position.offset( -20f, speedy*fps.toFloat())
        }
    }

    fun takeShot(vague: Int): Boolean{
        val multiplier: Int = if (vague <= 3) vague else 3
        val random = Random()
        val randomNumber: Int = random.nextInt(150/multiplier)
        return (randomNumber == 0 && position.top >= 0)
    }

    open fun shoot(player : RectF, bulletMiniBoss: RectF): Float{
        return 0f
    }
}