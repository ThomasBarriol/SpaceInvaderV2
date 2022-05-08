package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*
import java.util.ArrayList

class ShipJoueur(private var screenX: Int, var screenY: Int, w: Float, h: Float): drawable{

    var width = w
    var height = h

    val position = RectF(screenX /2f - width/2f, screenY - height, screenX /2f + width/2f, screenY.toFloat())

    var moving = 0

    private val speed = 500f

    fun update(fps: Double){
        if (moving == 1 && position.left >= 0){
            position.offset(-speed * fps.toFloat(), 0f)
        }
        else if(moving == 2 && position.left <= screenX){
            position.offset(speed * fps.toFloat(), 0f)
        }
        if (position.left < 0){
            position.offset(10f, 0f)
        }
        else if (position.right > screenX){
            position.offset(-10f, 0f)
        }
    }

    fun tire (playerBullets : ArrayList<Bullet>, bonus: Int) {
        when (bonus) {
            1 -> {
                playerBullets.add(
                    Bullet(
                        screenY,
                        position.centerX(),
                        position.top,
                        -1
                    )
                )
            }
            2 -> {
                playerBullets.add(
                    Bullet(
                        screenY,
                        position.centerX() - 20,
                        position.top,
                        -1
                    )
                )
                playerBullets.add(
                    Bullet(
                        screenY,
                        position.centerX() + 20,
                        position.top,
                        -1
                    )
                )
            }
            3 -> {
                playerBullets.add(
                    Bullet(
                        screenY,
                        position.centerX(),
                        position.top - 50,
                        -1
                    )
                )
                playerBullets.add(
                    Bullet(
                        screenY,
                        position.centerX() - 20,
                        position.top,
                        -1
                    )
                )
                playerBullets.add(
                    Bullet(
                        screenY,
                        position.centerX() + 20,
                        position.top,
                        -1
                    )
                )
            }
        }
    }
}