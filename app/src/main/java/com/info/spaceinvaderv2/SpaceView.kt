package com.info.spaceinvaderv2

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.SurfaceView

class SpaceView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, defStyleAttr : Int = 0): SurfaceView(context, attributes, defStyleAttr), Runnable {
    // Initialisation d'un point qui sera composer de la largeur de l'écran en x, et la hauteur en y
    val displayMetrics = DisplayMetrics()
    var w = context.resources.displayMetrics.widthPixels
    var h = context.resources.displayMetrics.heightPixels
    var size: Point = Point(w, h)

    // Initialisation des composants principaux qui permettent la gestion graphique du jeu
    private var thread = Thread(this)
    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()
    private var backgroundPaint: Paint = Paint()

    // Initialisation des variables booléennes qui controleront le fait de jouer ou d'être en pause
    private var playing = true
    private var paused = false
    private var gameover = false

    // Initialisation du joueur
    private var player: ShipJoueur = ShipJoueur(context, size.x, size.y)
    // Initialisation des bullets
    private var playerBullets = ArrayList<Bullet>()
    private var timeBetweenShots = 1f
    private var timeElapsed: Double = 0.0

    // Initialisation des variables générales spécifiques à notre jeu
    private var score = 0
    private var vies = 3
    private var vague = 1

    init {
        backgroundPaint.color = Color.BLACK
    }

    override fun run(){
        var previousFrameTime = System.currentTimeMillis()
        while (playing) {
            val currentTime = System.currentTimeMillis()
            var elapsedTimeMS:Double=(currentTime-previousFrameTime).toDouble()
            timeElapsed += elapsedTimeMS / 1000.0
            if (!paused){
                update(elapsedTimeMS)
            }
            draw()
            previousFrameTime = currentTime
        }
    }

    private fun draw(){
        if(holder.surface.isValid){
            // On bloque le canvas pour pouvoir dessiner dessus
            canvas = holder.lockCanvas()

            // Dessine le background
            canvas.drawColor(backgroundPaint.color)

            // On change la  couleur
            paint.color = Color.WHITE

            // Dessine le joueur
            canvas.drawBitmap(player.bitmap, player.position.left, player.position.top, paint)

            // Dessine les bullets si elles sont actives
            for (bullet in playerBullets){
                if (bullet.isActive){
                    bullet.draw(canvas)
                }
            }

            // On dessine le texte
            paint.textSize = 70f
            canvas.drawText("Score : $score     Vies : $vies    Vague : $vague", 20f, 75f, paint)

            // On dessine tous sur le canvas et on le débloque
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun update(fps: Double) {
        // Update tous les composants du jeu

        // Update le vaisseau du joueur
        player.update(fps)

        if (timeElapsed >= timeBetweenShots){
            shot(player.position.left + player.width/2, player.position.top)
            timeElapsed = 0.0
        }

        for (bullet in playerBullets){
            if (bullet.isActive){
                bullet.update(fps)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event?.action){
            MotionEvent.ACTION_MOVE -> if (playing){
                if (event.x < player.position.left){
                    player.moving = 1
                }
                else if (event.x > player.position.right){
                    player.moving = 2
                }
            }
            MotionEvent.ACTION_UP -> if (playing){
                player.moving = 0
            }
        }
        return true
    }

    fun shot(x: Float, y: Float){
        playerBullets.add(Bullet(size.y, x, y, -1))
    }

    fun pause(){
        playing = false
        thread.join()
    }

    fun resume(){
        playing = true
        thread.start()
    }
}