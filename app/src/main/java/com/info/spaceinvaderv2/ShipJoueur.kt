package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*

class ShipJoueur(context: Context, private var screenX: Int, var screenY: Int) {
    // Création du Bitmap qui représente le joueur
    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources,
        R.drawable.joueur
    )

    val width = screenX / 12f
    private val height = screenY / 20f
    val position = RectF(screenX /2f - width/2f, screenY - height, screenX /2f + width/2f, screenY.toFloat())

    var moving = 0

    private val speed = 500f

    init {
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
    }

    fun update(fps: Double){
        if (moving == 1){
            position.offset(-speed * fps.toFloat(), 0f)
        }
        else if(moving == 2){
            position.offset(speed * fps.toFloat(), 0f)
        }
        if (position.left < 0){
            position.offset(10f, 0f)
        }
        else if (position.right > screenX){
            position.offset(-10f, 0f)
        }
    }

    fun IsHit(bullet: Bullet):Boolean{
        bullet.isActive = false
        return bullet.position.intersect(position)
    }
}