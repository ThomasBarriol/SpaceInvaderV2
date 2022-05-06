package com.info.spaceinvaderv2

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.os.Bundle
import android.util.*
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

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
    private var bitmapInvader: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.invader1)
    private var bitmapMiniBoss: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.invader_miniboss)
    private var bitmapPlayer: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.joueur)
    private val activity = context as FragmentActivity

    // Initialisation des variables booléennes qui controleront le fait de jouer ou d'être en pause
    private var playing = true
    private var paused = false
    private var gameover = false

    // Initialisation du joueur
    private var player: ShipJoueur = ShipJoueur(context, size.x, size.y)

    // Initialisation des invaders
    private var invaders = ArrayList<Invader>()
    private var numInvaders = 4

    // Initialisation des bullets du joueur
    private var playerBullets = ArrayList<Bullet>()
    private var timeBetweenShots = 0.9f
    private var timeElapsed: Double = 0.0

    // Initialisation des bullets ennemies
    var invadersBullets= ArrayList<Bullet>()

    // Initialisation des variables générales spécifiques à notre jeu
    private var score = 0
    private var vies = 3
    private var vague = 1

    init {
        backgroundPaint.color = Color.BLACK
        bitmapInvader = Bitmap.createScaledBitmap(bitmapInvader, w/10, h/15, false)
        bitmapMiniBoss = Bitmap.createScaledBitmap(bitmapMiniBoss, w/5,h/10, false)
        bitmapPlayer = Bitmap.createScaledBitmap(bitmapPlayer, w/ 12, h/20, false)
    }

    override fun run(){
        initialisationNiveau(vague)
        var previousFrameTime = System.currentTimeMillis()
        while (playing) {
            val currentTime = System.currentTimeMillis()
            var elapsedTimeMS:Double=(currentTime-previousFrameTime).toDouble()
            timeElapsed += elapsedTimeMS / 1000.0
            if (!paused){
                update(elapsedTimeMS/1000.0)
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
            paint.color = Color.WHITE

            // Dessine le joueur
            player.draw(canvas, bitmapPlayer, player.position, paint)

            // Dessine les bullets du joueur si elles sont actives
            for (bullet in playerBullets){
                if (bullet.isActive){
                    bullet.draw(canvas)
                }
            }

            // Dessine les bullets des ennemies si elles sont actives
            for (bullet in invadersBullets){
                if (bullet.isActive){
                    bullet.draw(canvas)
                }
            }

            // Dessine les invaders
            for (invader in invaders){
                if (invader.isVisible)
                    invader.draw(canvas, bitmapInvader, invader.position, paint)
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

        //upodate les déplacements du joueur
        player.update(fps)

        for (invader in invaders) {
            if (invader.isVisible) {
                invader.updateMove(fps, vague)
                if (invader.takeShot(vague)) invadersBullets.add(
                    Bullet(
                        size.y,
                        invader.position.left + invader.width / 2,
                        invader.position.bottom,
                        1
                    )
                )
            }
        }
        // Toutes les secondes, le joueur tire
        if (timeElapsed >= timeBetweenShots){
            playerBullets.add(Bullet(size.y, player.position.left + player.width/2, player.position.top, -1))
            timeElapsed = 0.0
        }

        // update des bullets du joueur
        for (bullet in playerBullets){
            if (bullet.isActive){
                bullet.update(fps)
                for (invader in invaders){
                    if (invader.isVisible && bullet.position.intersect(invader.position)){
                        numInvaders --
                        score += 10*invader.type
                        bullet.isActive = false
                        invader.isVisible = false
                    }

                }
            }
        }

        for (bullet in invadersBullets){
            if (bullet.isActive){
                bullet.update(fps)
                if (bullet.position.intersect(player.position)){
                    vies --
                    bullet.isActive = false
                }
            }
        }

        // on verifie sur le joueur a tué tous les invaders de la vague
        if (numInvaders == 0) {
            playing = false
            NewVague()
        }
        else if (vies == 0){
            playing = false
            showGameOverDialog(R.string.lose)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event?.action){
            MotionEvent.ACTION_MOVE -> if (playing){
                if (event.x < player.position.left - player.width){
                    player.moving = 1
                }
                else if (event.x > player.position.right + player.width){
                    player.moving = 2
                }
            }
            MotionEvent.ACTION_UP -> if (playing){
                player.moving = 0
            }
        }
        return true
    }

    fun initialisationNiveau(vague: Int){
        invaders.add(Invader(context, w/4.toFloat(), 200f , size.x, size.y,bitmapInvader))
        invaders.add(Invader(context, 3*w/4.toFloat(), 200f , size.x, size.y, bitmapInvader))
        invaders.add(Invader(context, w/4.toFloat(), size.y/2.toFloat() - 200f, size.x, size.y, bitmapInvader))
        invaders.add(Invader(context, 3*w/4.toFloat(), size.y/2.toFloat() - 200f, size.x, size.y, bitmapInvader))
    }

    fun showGameOverDialog(messageId: Int) {
        class GameResult: DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(resources.getString(messageId))
                builder.setMessage("Score : ${score} \nVague :" +
                        " ${vague}"
                )
                builder.setPositiveButton(R.string.reset_game,
                    DialogInterface.OnClickListener { _, _->newGame()}
                )
                return builder.create()
            }
        }

        activity.runOnUiThread(
            Runnable {
                val ft = activity.supportFragmentManager.beginTransaction()
                val prev =
                    activity.supportFragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val gameResult = GameResult()
                gameResult.setCancelable(false)
                gameResult.show(ft,"dialog")
            }
        )
    }

    fun newGame(){
        vague = 0
        score = 0
        playerBullets.clear()
        invadersBullets.clear()
        initialisationNiveau(vague)
        playing = true
    }

    fun NewVague(){
        playerBullets.clear()
        invadersBullets.clear()
        invaders.clear()
        vies ++
        vague ++
        numInvaders ++
        initialisationNiveau(vague)
        playing = true
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