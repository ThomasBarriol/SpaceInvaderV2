package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*
import java.util.ArrayList

class ShipJoueur(private var screenX: Int, var screenY: Int, w: Float, h: Float): drawable{

    var width = w
    var height = h

    val position = RectF(screenX /2f - width/2f, screenY - height, screenX /2f + width/2f, screenY.toFloat())

    fun update(){
        if (position.left <= 0){
            position.left = 1f
            position.right = position.left + width
        }
        else if (position.right >= screenX){
            position.right = screenX - 1f
            position.left = position.right - width
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