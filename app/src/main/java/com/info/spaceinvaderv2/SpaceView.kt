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
import java.lang.Thread.sleep
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class SpaceView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, defStyleAttr : Int = 0): SurfaceView(context, attributes, defStyleAttr), Runnable {
    // Initialisation d'un point qui sera composer de la largeur de l'écran en x, et la hauteur en y
    val displayMetrics = DisplayMetrics()
    private var w = context.resources.displayMetrics.widthPixels
    private var h = context.resources.displayMetrics.heightPixels
    private var size: Point = Point(w, h)

    // Initialisation des composants principaux qui permettent la gestion graphique du jeu
    private var thread = Thread(this)
    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()
    private var backgroundPaint: Paint = Paint()
    private var bitmapInvader: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.invader)
    private var bitmapMiniBoss: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.invader_miniboss)
    private var bitmapPlayer: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.joueur)
    private val activity = context as FragmentActivity
    private val random = Random()
    private var newVagueMess : Boolean = true
    private var timeElapsedMess : Double = 0.0
    private val timeMess : Double = 5.0

    // Initialisation des variables booléennes qui controleront le fait de jouer ou d'être en pause
    var playing = true
    var paused = false
    private var gameover = false

    // Initialisation du joueur
    private var player: ShipJoueur = ShipJoueur(context, size.x, size.y)
    private val timeImmune : Double = 3.0
    private var timeElapsedImmune: Double = 0.0
    var widthPlayer : Int = w/6
    var heightPlayer : Int = h/10

    // Initialisation des invaders
    private var invaders = ArrayList<Invader>()
    private val numInvadersInit = 2
    private var numInvaders = numInvadersInit
    var widthInvader : Int = w/8
    var heightInvader : Int = h/15
    var widthMiniBoss: Int = w/5
    var heightMiniBoss: Int = h/10

    // Initialisation des bullets du joueur
    private var playerBullets = ArrayList<Bullet>()
    private var timeBetweenShots = 0.9f
    private var timeElapsedShoot: Double = 0.0

    // Initialisation des bullets ennemies
    var invadersBullets= ArrayList<Bullet>()

    // Initialisation des variables générales spécifiques à notre jeu
    var score = 0
    private var viesinit = 1
    private var vies = viesinit
    private var vague = 1

    init {
        backgroundPaint.color = Color.BLACK
        bitmapInvader = Bitmap.createScaledBitmap(bitmapInvader, widthInvader, heightInvader, false)
        bitmapMiniBoss = Bitmap.createScaledBitmap(bitmapMiniBoss, widthMiniBoss,heightMiniBoss, false)
        bitmapPlayer = Bitmap.createScaledBitmap(bitmapPlayer, widthPlayer, heightPlayer, false)
        initialisationNiveau(vague)
    }

    override fun run(){
        var previousFrameTime = System.currentTimeMillis()
        while (playing) {
            val currentTime = System.currentTimeMillis()
            var elapsedTimeMS:Double=(currentTime-previousFrameTime).toDouble()
            timeElapsedShoot += elapsedTimeMS / 1000.0
            timeElapsedImmune += elapsedTimeMS /1000.0
            timeElapsedMess += elapsedTimeMS / 1000.0
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
                    when (invader.type){
                        1 -> invader.draw(canvas, bitmapInvader, invader.position, paint)
                        5 -> {invader.draw(canvas, bitmapMiniBoss, invader.position, paint)
                        paint.textAlign = Paint.Align.CENTER
                        paint.textSize = 60f
                        canvas.drawText("${invader.life}", invader.position.left + widthMiniBoss/2, invader.position.top - 50f, paint)}
                    }

            }

            // On dessine le texte
            paint.textSize = 60f
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("Score : $score     Vies : $vies    Vague : $vague  Invaders : $numInvaders", 20f, 75f, paint)

            if (newVagueMess){
                paint.textAlign = Paint.Align.CENTER
                paint.textSize = 200f
                canvas.drawText("Vague $vague", w/2f, h/4f, paint)
                if (timeElapsedMess >= timeMess){
                    timeElapsedMess = 0.0
                    newVagueMess = false
                }
            }
            // On dessine tous sur le canvas et on le débloque
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun update(fps: Double) {
        // Update tous les composants du jeu

        // update les déplacements du joueur
        player.update(fps)

        // update les invaders, tire si ils en ont la chance et on vérifie si un invader a atteint le bas de la page,
        //  si oui, le joueur a perdu
        for (invader in invaders) {
            if (invader.isVisible) {
                invader.updateMove(fps, vague)
                if (invader.takeShot(vague)) {
                    when (invader.type){
                        1 -> invadersBullets.add(
                            Bullet(size.y, invader.position.centerX(), invader.position.bottom, 1)
                        )
                        5 -> {
                            val directionality =  invader.shoot(player.position, invader.position)
                            invadersBullets.add(BulletMiniBoss(size.y,
                                invader.position.centerX(),
                                invader.position.bottom,
                                1,700f,30f,directionality))
                        }
                    }

                }
            }
        }
        // Toutes les secondes, le joueur tire
        if (timeElapsedShoot >= timeBetweenShots){
            playerBullets.add(Bullet(size.y, player.position.left + player.width/2, player.position.top, -1))
            timeElapsedShoot = 0.0
        }

        // Update les bullets du joueur et détecte si elles touchent un invader,
        // si oui, enlève une vie à l'invader, augmente le score
        for (bullet in playerBullets){
            if (bullet.isActive){
                bullet.update(fps)
                for (invader in invaders){
                    if (invader.isVisible && bullet.position.intersect(invader.position)){
                        invader.life --
                        bullet.isActive = false
                        score += 10*invader.type
                        if (invader.life == 0){
                            numInvaders --
                            invader.isVisible = false
                        }
                    }
                }
            }
        }

        // On vérifie si les invaders ont touché le joueur, si oui alors il perd une vie et est immunisé
        for (bullet in invadersBullets){
            if (bullet.isActive){
                bullet.update(fps)
                if (bullet.position.intersect(player.position) && timeElapsedImmune >= timeImmune){
                    vies --
                    bullet.isActive = false
                    timeElapsedImmune = 0.0
                }
            }
        }

        // on verifie sur le joueur a tué tous les invaders de la vague ou si il a plus de vie
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
            MotionEvent.ACTION_MOVE -> if (playing && player.position.left >= 0 && player.position.right <= w.toFloat()){
                if (event.x < player.position.left + player.width){
                    player.moving = 1
                }
                else if (event.x > player.position.right - player.width){
                    player.moving = 2
                }
                else player.moving = 0
            }
            MotionEvent.ACTION_UP -> if (playing){
                player.moving = 0
            }
        }
        return true
    }

    fun initialisationNiveau(vague: Int){

        if (vague % 2 == 0){
            for (i in 1..numInvaders){
                var moving = random.nextInt(1)
                moving = if (moving == 0) -1 else moving
                invaders.add(Invader((0..w).random().toFloat(),(0..h/4).random().toFloat(), w, h, widthInvader, heightInvader, moving))
            }
            var moving = random.nextInt(1)
            moving = if (moving == 0) -1 else moving
            invaders.add(Miniboss(w/2f, h/4f, w, h, moving, widthMiniBoss, heightMiniBoss ))
            numInvaders ++

        }
        else {
            for (i in 1..numInvaders){
                var moving = random.nextInt(1)
                moving = if (moving == 0) -1 else moving
                invaders.add(Invader((0..w).random().toFloat(),(0..h/4).random().toFloat(), w, h, widthMiniBoss, heightMiniBoss, moving))
            }
        }
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
                    DialogInterface.OnClickListener { _, _->newGame()})
                builder.setNegativeButton("menu", DialogInterface.OnClickListener { _, _-> exitProcess(-1) })
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
        vague = 1
        score = 0
        vies = viesinit
        timeElapsedShoot = 0.0
        timeElapsedImmune = 0.0
        numInvaders = numInvadersInit
        playerBullets.clear()
        invadersBullets.clear()
        invaders.clear()
        initialisationNiveau(vague)
        playing = true
        paused = false
        thread = Thread(this)
        thread.start()
    }

    fun NewVague(){
        timeElapsedImmune = 0.0
        playerBullets.clear()
        invadersBullets.clear()
        invaders.clear()
        vies ++
        vague ++
        timeBetweenShots -= 0.05f
        numInvaders = if (vague <= 10) numInvadersInit + vague - 1 else 11
        initialisationNiveau(vague)
        newVagueMess = true
        playing = true
    }

    fun pause(){
        playing = false
        thread.join()
    }

    fun resume(){
        playing = true
        thread = Thread(this)
        thread.start()
    }
}